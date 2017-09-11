jQuery(function($) {

    var DEFAULT_DURATION = 2;

    var dragged = null;
    var viewDate = moment().subtract(7, 'days');

    function formatTime(time) {
        return (time < 13 ? time : (time - 12)) + ' ' +
            (time < 12 ? 'AM' : 'PM');
    }

    function formatTimeSpan(time, duration) {
        return formatTime(time) + ' - ' + formatTime(time + duration);
    }

    function createEvent(slot) {
        var column = slot.parent();
        var date = column.attr('data-date');
        var time = parseInt(slot.attr('data-time'));
        var duration = DEFAULT_DURATION;

        var begin = $('<div class="begin"></div>');
        var end   = $('<div class="end"></div>');
        var body  = $('<span class="event-body"></span>')
            .text(formatTimeSpan(time, duration));

        var event = $('<div class="calendar-event"></div>')
            .attr('data-time', time)
            .attr('data-hours', duration)
            .addClass('pending')
            .append(begin)
            .append(body)
            .append(end);

        var dragEndHandler = function(ev) {
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
                    dragged.parent().addClass('pending');
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

        column.append(event);
        return event;
    }

    function renderWeek(startDate) {
        $('.calendar > .calendar-weekday').remove();
        for (var i = 0; i < 7; i++) {
            var m = startDate.add(i, 'days');
            var weekday = $('<div class="col calendar-weekday">')
                .attr('data-date', m.format('YYYY-MM-DD'))
                .append($('<div class="calendar-label">' +
                    m.format('ddd MMM Do') + '</div>'));

            for (var j = 8; j < 24; j++) {
                var slot = $('<div class="calendar-slot"></div>')
                   .attr('data-time', j);

                slot.click(function(ev) {
                    if (dragged === null) {
                        createEvent($(this));
                    } else {
                        dragged = null;
                    }

                    ev.preventDefault();
                });

                slot.mousedown(function(ev) {
                    if (dragged === null) {
                        dragged = createEvent($(this));
                    } else {
                        dragged = null;
                    }

                    ev.preventDefault();
                });

                slot.mousemove(function(ex) {
                    var column = $(this).parent();
                    var date = column.attr('data-date');
                    var time = parseInt($(this).attr('data-time'));

                    if (dragged !== null) {
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
                                .text(formatTimeSpan(time, DEFAULT_DURATION));
                        }

                        if (dragged.hasClass('begin')) {
                            var event = dragged.parent();
                            updateDuration(event);
                            updateFrom(event);
                            updateText(event);

                        } else if (dragged.hasClass('end')) {
                            var event = dragged.parent();
                            updateTo(event);
                            updateText(event);

                        } else {
                            updateFrom(dragged);
                            updateText(dragged);
                            updateDate(dragged);
                        }
                    }
                });

                weekday.append(slot);
            }

            $('.calendar').append(weekday);
        }
    }

    renderWeek(viewDate);

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
            console.log(JSON.parse(event.body).content);
        });
    });
});