package somethingspecific.face2face.events

public class Event<T> {
    private val handlers = arrayListOf<(Event<T>.(T) -> Unit)>()

    operator fun plusAssign(handler: Event<T>.(T) -> Unit) {
        handlers.add(handler)
    }

    operator fun invoke(value: T) {
        for (handler in handlers)
            handler(value)
    }
}
public class EmptyEvent {
    private val handlers = arrayListOf<(EmptyEvent.() -> Unit)>()

    operator fun plusAssign(handler: EmptyEvent.() -> Unit) {
        handlers.add(handler)
    }

    operator fun invoke() {
        for (handler in handlers)
            handler()
    }
}