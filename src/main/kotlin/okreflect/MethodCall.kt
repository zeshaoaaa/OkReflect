package okreflect

class MethodCall(val methodName: String,
                 var classes: Array<Class<*>>?,
                 val callWithInstance: Boolean,
                 val args: Array<out Any?>)
