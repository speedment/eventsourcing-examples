jQuery(function($) {

    function colorize(number) {
        return '#' + Math.floor((Math.abs(Math.sin(number) * 16777215)) % 16777215).toString(16);
    }

    function formatHour(hour) {
        return (hour < 10 ? ('0' + hour) : hour) + ':00';
    }

    function formatTime(time) {
        return (time < 13 ? time : (time - 12)) + ' ' +
            (time < 12 ? 'AM' : 'PM');
    }

    function formatTimeSpan(time, duration) {
        return formatTime(time) + ' - ' + formatTime(time + duration);
    }

    var CalenderView = function(selector) {

        // Member variables
        this.calendar = $(selector);
        this.dragged  = null;

        // Remove all events from the view
        this.clear = function() {
            this.calendar.find('.calendar-event').remove();
        }

        // Render a calendar view of the specified time period. from and to are
        // moments.
        this.render = function(from, to) {
            var self = this;
            self.clear();

            var dayCount = moment.duration(to.diff(from)).asDays();
            var day = moment(from);

            for (var i = 0; i < dayCount; i++) {

                var weekday = $('<div class="col calendar-weekday">')
                    .attr('data-date', day.format('YYYY-MM-DD'))
                    .append($('<div class="calendar-label">' +
                        day.format('ddd MMM Do') + '</div>'));

                for (var j = 8; j < 24; j++) {
                    var slot = $('<div class="calendar-slot"></div>')
                       .attr('data-time', j);

                    slot.click(function(ev) {
                        if (self.dragged === null) {
                            var userId = 600;
                            var mFrom  = moment(weekday.attr('data-date') + 'T' + formatHour(slot.attr('data-time')) + '+01:00');
                            var mTo    = moment(weekday.attr('data-date') + 'T' + formatHour(parseInt(slot.attr('data-time')) + 2) + '+01:00');

                            console.log('TODO: Send Create Event to REST API');
                            self.dragged = self.addEvent(null, colorize(userId), mFrom, mTo).addClass('pending');
                        } else {
                            self.dragged = null;
                        }

                        ev.preventDefault();
                    });

                    slot.mousedown(function(ev) {
                        if (self.dragged === null) {
                            var userId = 600;
                            var mFrom  = moment(weekday.attr('data-date') + 'T' + formatHour(slot.attr('data-time')) + '+01:00');
                            var mTo    = moment(weekday.attr('data-date') + 'T' + formatHour(parseInt(slot.attr('data-time')) + 2) + '+01:00');

                            console.log('TODO: Send Create Event to REST API');
                            self.dragged = self.addEvent(null, colorize(userId), mFrom, mTo).addClass('pending');
                        } else {
                            self.dragged = null;
                        }

                        ev.preventDefault();
                    });

                    slot.mousemove(function(ex) {
                        var date = weekday.attr('data-date');
                        var time = parseInt(slot.attr('data-time'));

                        if (self.dragged !== null) {
                            function updateDuration(event) {
                                var currentStart = parseInt(event.attr('data-time'));
                                var currentEnd = currentStart + parseInt(event.attr('data-hours'));
                                event.attr('data-hours', currentEnd - time);
                            }

                            function updateFrom(event) {
                                event.attr('data-time', time);
                            }

                            function updateTo(event) {
                                event.attr('data-hours', Math.max(time - event.attr('data-time'), 0) + 1);
                            }

                            function updateDate(event) {
                                if (event.parent().attr('data-date') !== date) {
                                    column.append(event.detach());
                                }
                            }

                            function updateText(event) {
                                event.children('.event-body')
                                    .text(formatTimeSpan(time, parseInt(event.attr('data-hours'))));
                            }

                            if (dragged.hasClass('begin')) {
                                var event = self.dragged.parent();
                                updateDuration(event);
                                updateFrom(event);
                                updateText(event);

                            } else if (self.dragged.hasClass('end')) {
                                var event = self.dragged.parent();
                                updateTo(event);
                                updateText(event);

                            } else {
                                updateFrom(self.dragged);
                                updateText(self.dragged);
                                updateDate(self.dragged);
                            }
                        }
                    });

                    weekday.append(slot);
                }

                console.log('calendar');
                console.log(this.calendar);
                console.log('weekday');
                console.log(weekday);
                this.calendar.append(weekday);
                day.add(1, 'days');
            }

            // Populate week view by invoking REST API
            $.ajax({
                url: '/api/v1/schedule/',
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                success: function(data) {
                    console.log('Rendering ' + data.length + ' bookings.');
                    for (var i = 0; i < data.length; i++) {
                        self.addEvent(null,
                            colorize(data.userId),
                            moment(data.fromIncl),
                            moment(data.toExcl)
                        );
                    }
                }
            });
        }

        // Add an event already loaded to the view
        this.addEvent = function(uuid, color, from, to) {
            var weekday = this.calendar.find('.calendar-weekday[data-date="' + from.format('YYYY-MM-DD') + '"]');

            var time = moment.duration(from.diff(moment(from).hours(8).minutes(0).seconds(0))).asHours();
            var duration = moment.duration(to.diff(from)).asHours();

            var begin = $('<div class="begin"></div>');
            var end   = $('<div class="end"></div>');
            var body  = $('<span class="event-body"></span>')
                .text(formatTimeSpan(time, duration));

            var event = $('<div class="calendar-event"></div>')
                .attr('data-time', time)
                .attr('data-hours', duration)
                .append(begin)
                .append(body)
                .append(end)
                .addClass('pending');

            var dragEndHandler = function(ev) {
                if (dragged !== null) {
                    // TODO: Send update event to REST API and add pending.
                }

                dragged = null;
                ev.preventDefault();
                ev.stopPropagation();
            };

            var deleteHandler = function(ev) {
                if (dragged === null) {
                    event.remove();
                } else {
                    dragged = null;
                }

                ev.preventDefault();
                ev.stopPropagation();
            };

            var dragStartHandler = function(ev) {
                if (event.which === 3) {
                    deleteHandler(ev);
                } else {
                    if (dragged === null) {
                        dragged = $(this);
                        event.addClass('pending');
                    } else {
                        dragged = null;
                    }

                    ev.preventDefault();
                    ev.stopPropagation();
                }
            };

            event.mousedown(dragStartHandler);
            begin.mousedown(dragStartHandler);
            end.mousedown(dragStartHandler);

            begin.mouseup(dragEndHandler);
            end.mouseup(dragEndHandler);
            event.mouseup(dragEndHandler);

            begin.contextmenu(deleteHandler);
            end.contextmenu(deleteHandler);
            event.contextmenu(deleteHandler);

            weekday.append(event);
            return event;
        }

        // Change an event already present in the view
        this.updateEvent = function(uuid, color, from, to) {

        }

        // Delete an event from the view
        this.deleteEvent = function(uuid) {

        }
    }
//
//    var DEFAULT_DURATION = 2;
//
//    var dragged = null;
//    var viewDate = moment().subtract(7, 'days');
//
//    function formatTime(time) {
//        return (time < 13 ? time : (time - 12)) + ' ' +
//            (time < 12 ? 'AM' : 'PM');
//    }
//
//    function formatTimeSpan(time, duration) {
//        return formatTime(time) + ' - ' + formatTime(time + duration);
//    }
//
//    function showError(msg) {
//        var box = $('<div class="alert alert-danger" role="alert"></div>')
//            .text(msg).hide();
//
//        $('.calendar').append(box);
//        box.fadeIn().delay(5000).fadeOut('slow').remove();
//
//        console.log('Error: ' + msg);
//    }
//
//    function createEvent(slot, duration) {
//        var column = slot.parent();
//        var date = column.attr('data-date');
//        var time = parseInt(slot.attr('data-time'));
//
//        duration = typeof duration !== 'undefined' ? duration : DEFAULT_DURATION;
//
//        var begin = $('<div class="begin"></div>');
//        var end   = $('<div class="end"></div>');
//        var body  = $('<span class="event-body"></span>')
//            .text(formatTimeSpan(time, duration));
//
//        var event = $('<div class="calendar-event"></div>')
//            .attr('data-time', time)
//            .attr('data-hours', duration)
//            .append(begin)
//            .append(body)
//            .append(end)
//            .addClass('pending');
//
//        var dragEndHandler = function(ev) {
//            if (dragged !== null) {
//                window.setTimeout(function(){event.removeClass('pending');},2000);
//            }
//
//            dragged = null;
//            ev.preventDefault();
//            ev.stopPropagation();
//        };
//
//        var deleteHandler = function(ev) {
//            if (dragged === null) {
//                event.remove();
//            } else {
//                dragged = null;
//            }
//
//            ev.preventDefault();
//            ev.stopPropagation();
//        };
//
//        var dragStartHandler = function(ev) {
//            if (event.which === 3) {
//                deleteHandler(ev);
//            } else {
//                if (dragged === null) {
//                    dragged = $(this);
//                    event.addClass('pending');
//                } else {
//                    dragged = null;
//                }
//
//                ev.preventDefault();
//                ev.stopPropagation();
//            }
//        };
//
//        event.mousedown(dragStartHandler);
//        begin.mousedown(dragStartHandler);
//        end.mousedown(dragStartHandler);
//
//        begin.mouseup(dragEndHandler);
//        end.mouseup(dragEndHandler);
//        event.mouseup(dragEndHandler);
//
//        begin.contextmenu(deleteHandler);
//        end.contextmenu(deleteHandler);
//        event.contextmenu(deleteHandler);
//
//        column.append(event);
//        window.setTimeout(function(){event.removeClass('pending');},2000);
//        return event;
//    }
//
//    function sendCreateEvent(slot, duration) {
//        var result = createEvent(slot, duration);
//
//        $.ajax({
//            url: '/api/v1/booking/',
//            type: 'POST',
//            data: JSON.stringify({
//                'userId': 654,
//                'resource': 'Room A',
//                'bookFrom': '2017-01-01T10:00:00+01:00',
//                'bookTo': '2017-01-01T12:00:00+01:00'
//            }),
//            contentType: 'application/json',
//            dataType: 'json',
//            success: function(data) {
//                console.log(data.eventId);
//            }
//        });
//
//        return result;
//    }
//
//    function renderWeek(startDate) {
//        $('.calendar > .calendar-weekday').remove();
//        for (var i = 0; i < 7; i++) {
//            var m = startDate.add(1, 'days');
//            var weekday = $('<div class="col calendar-weekday">')
//                .attr('data-date', m.format('YYYY-MM-DD'))
//                .append($('<div class="calendar-label">' +
//                    m.format('ddd MMM Do') + '</div>'));
//
//            for (var j = 8; j < 24; j++) {
//                var slot = $('<div class="calendar-slot"></div>')
//                   .attr('data-time', j);
//
//                slot.click(function(ev) {
//                    if (dragged === null) {
//                        sendCreateEvent($(this));
//                    } else {
//                        dragged = null;
//                    }
//
//                    ev.preventDefault();
//                });
//
//                slot.mousedown(function(ev) {
//                    if (dragged === null) {
//                        dragged = sendCreateEvent($(this));
//                    } else {
//                        dragged = null;
//                    }
//
//                    ev.preventDefault();
//                });
//
//                slot.mousemove(function(ex) {
//                    var column = $(this).parent();
//                    var date = column.attr('data-date');
//                    var time = parseInt($(this).attr('data-time'));
//
//                    if (dragged !== null) {
//                        function updateDuration(event) {
//                            var currentStart = parseInt(event.attr('data-time'));
//                            var currentEnd = currentStart + parseInt(event.attr('data-hours'));
//                            event.attr('data-hours', currentEnd - time);
//                        }
//
//                        function updateFrom(event) {
//                            event.attr('data-time', time);
//                        }
//
//                        function updateTo(event) {
//                            event.attr('data-hours', Math.max(time - event.attr('data-time'), 0) + 1);
//                        }
//
//                        function updateDate(event) {
//                            if (event.parent().attr('data-date') !== date) {
//                                column.append(event.detach());
//                            }
//                        }
//
//                        function updateText(event) {
//                            event.children('.event-body')
//                                .text(formatTimeSpan(time, DEFAULT_DURATION));
//                        }
//
//                        if (dragged.hasClass('begin')) {
//                            var event = dragged.parent();
//                            updateDuration(event);
//                            updateFrom(event);
//                            updateText(event);
//
//                        } else if (dragged.hasClass('end')) {
//                            var event = dragged.parent();
//                            updateTo(event);
//                            updateText(event);
//
//                        } else {
//                            updateFrom(dragged);
//                            updateText(dragged);
//                            updateDate(dragged);
//                        }
//                    }
//                });
//
//                weekday.append(slot);
//            }
//
//            $('.calendar').append(weekday);
//        }
//    }
//
//    renderWeek(viewDate);

    var calenderView = new CalenderView('.calendar');
    calenderView.render(
        moment('2017-09-01'),
        moment('2017-09-08')
    );

    $('.earlier').click(function(ev) {
        viewDate = viewDate.subtract(7, 'days');
        renderWeek(viewDate);
        ev.preventDefault();
    });

    $('.later').click(function(ev) {
        viewDate = viewDate.add(7, 'days');
        renderWeek(viewDate);
        ev.preventDefault();
    });

    var socket = new SockJS('http://localhost:9091/subscribe');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected.');
        stompClient.subscribe('/schedule', function (event) {
            var notification = JSON.parse(event.body);
            if (notification.status === 'REJECTED') {
                console.log('Event rejected: ' + notification.data);
                showError(notification.msg);
            } else if (notification.status === 'ACCEPTED') {
                console.log('Event accepted: ' + notification.data);

                var mFrom = moment(notification.data.bookFrom);
                var mTo   = moment(notification.data.bookTo);

                var dateFrom = mFrom.format('YYYY-MM-DD');
                var duration = moment.duration(mTo.diff(mFrom)).asHours();

                var day = $('.calendar-weekday[data-date="' + mFrom.format('YYYY-MM-DD') + '"]');
                if (day.length === 0) {
                    console.error('Can\'t find day for date "' + mFrom.format('YYYY-MM-DD') + '".');
                } else {
                    var slot = day.find('.calendar-slot[data-time="' + mFrom.format('HH') + '"]');
                    if (slot.length === 0) {
                        console.error('Can\'t find slot for date & time "' + mFrom.format('YYYY-MM-DD HH') + '".');
                    } else {
                        createEvent(slot, duration);
                    }
                }
            } else {
                console.error('Unknown notification status for ' + event.body);
            }
        });
    });
});