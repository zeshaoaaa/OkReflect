import java.lang.reflect.*

/**
 * OkReflect is a library that trying to help you use Java Reflect feature with ease.
 *
 * The reasons why I develop this library is because I run into some problem while using jOOR.
 * If you encounter some problem or you have some suggestion for me, you can post an issue,
 * I will solve it for you as soon as possible.
 *
 * @author <a href="">zeshaoaaa</a>
 *
 */
class OkReflect {

    /**
     * The className of the object that you want to create.
     */
    private var className: String? = null

    /**
     * The constructor parameters of the object that you want to create.
     */
    private var constructorArgs: Array<out Any>? = null

    /**
     * All the method that you called will store with this map.
     */
    private var methodCallList: LinkedHashMap<Pair<String, Boolean>, Array<out Any>>? = null

    /**
     * OnError method of this callback will be call when exception happen.
     */
    private var errorCallback: OkReflectErrorCallback? = null

    /**
     * Class of the object that you want to create.
     */
    private var clazz: Class<*>? = null

    /**
     * Instance of the object that you want to create.
     */
    private var instance: Any? = null

    /**
     * The result of the method that you called.
     */
    private var result: Any? = null

    /**
     * @param className: The name of the class that you want to create.
     *
     * Constructor of OkReflect.
     */
    constructor(className: String) {
        this.className = className
    }

    /**
     * @param clazz: The class object of the class that you want to create.
     *
     * Constructor of OkReflect.
     */
    constructor(clazz: Class<String>) {
        this.clazz = clazz
    }

    /**
     * Set the parameters of the constructor of the class that you want to create.
     */
    fun create(vararg args: Any): OkReflect {
        this.constructorArgs = args
        return this
    }

    /**
     * Initialized the instance.
     */
    private fun initInstance() {
        val constructor = getConstructor()
        val instance = if (constructorArgs == null) {
            constructor!!.newInstance()
        } else {
            constructor!!.newInstance(*constructorArgs!!)
        }
        this.instance = instance
    }

    /**
     * Get the constructor of the class.
     */
    private fun getConstructor(): Constructor<out Any>? {
        val declared =
            if (constructorArgs != null && constructorArgs!!.isNotEmpty()) {
                val types = getConversedParametersType(constructorArgs!!)
                clazz!!.getDeclaredConstructor(*types)
            } else {
                clazz!!.getDeclaredConstructor()
            }

        return accessible(declared)
    }

    /**
     * @param action: The action that you want to take when exception happen.
     *
     * Set the error callback for receive error message when exception happen.
     * If you have not set the callback, then you have catch exception by yourself.
     */
    fun error(action: (String) -> Unit): OkReflect {
        errorCallback = object : OkReflectErrorCallback {
            override fun onError(errorMsg: String) {
                action(errorMsg)
            }
        }
        return this
    }

    /**
     * @param okReflectErrorCallback: Exception callback.
     *
     * Set the error callback for receive error message when exception happen.
     * If you have not set the callback, then you have catch exception by yourself.
     */
    fun error(okReflectErrorCallback: OkReflectErrorCallback): OkReflect {
        this.errorCallback = okReflectErrorCallback
        return this
    }

    /**
     * @param methodName: the name of the method that you want to call.
     * @param args: The parameters of the method that you wan to call.
     *
     * Call the method that you want to call.
     * The method will be called when get() method called.
     * The method will be call with the instance.
     */
    fun call(methodName: String, vararg args: Any): OkReflect {
        return realCall(methodName, true, *args)
    }

    /**
     * @param methodName: the name of the method that you want to call.
     * @param args: The parameters of the method that you wan to call.
     *
     * Call the method that you want to call.
     * The method will be called when get() method called.
     * The method will be call with the return value from last method.
     */
    fun callWithResult(methodName: String, vararg args: Any): OkReflect {
        return realCall(methodName, false, *args)
    }

    /**
     * @param methodName: The name of the method that you want to call.
     * @param callWithInstance: You can call the method with instance or with the return value from last method.
     * @param args: The parameters of the method that you wan to call.
     *
     * Invoke the method with instance or the return type from last method.
     */
    private fun realCall(methodName: String, callWithInstance: Boolean = true, vararg args: Any): OkReflect {
        if (methodCallList == null) {
            methodCallList = LinkedHashMap()
        }
        val key = Pair(methodName, callWithInstance)
        methodCallList!![key] = args
        return this
    }

    /**
     * @param nameAndFrom: The first value is the method name,
     * the second value is whether you call the method by instance
     * or by the return value from the last method.
     * When the second value is true, it means you call the method with instnace.
     *
     * Invoke the the method that you called by call() method.
     */
    private fun invoke(nameAndFrom: Pair<String, Boolean>, vararg args: Any) {
        val method = getMethod(nameAndFrom.first, args)
        val withInstance = nameAndFrom.second
        val returnType = method!!.returnType.toString()
        if (returnType == "void") {
            method.invoke(instance, *args)
        } else {
            if (withInstance) {
                result = method.invoke(instance, *args)
            } else {
                if (result != null) {
                    result = method.invoke(result, *args)
                } else {
                    printError(
                        "you cannot call the method with the return value of last method" +
                                ", because it is null."
                    )
                }
            }
        }

    }

    /**
     * @param msg: The error message that you want to output.
     *
     * Print the exception message.
     * If you have already pass the ErrorCallback into onError method,
     * you will receive the error message from callback.
     */
    private fun printError(msg: String) {
        if (errorCallback != null) {
            errorCallback!!.onError("error: $msg")
        } else {
            throw Exception(msg)
            // Log.e("OkReflect", "error: $msg")
        }
    }

    /**
     * Get the instance or the return value of the method that you called.
     */
    fun <T> get(): T? {
        return try {
            realGet()
        } catch (e: Exception) {
            printError(e.cause.toString())
            null
        }
    }

    /**
     * Get the instance no matter result have value or not.
     */
    fun <T> getInstance(): T? {
        return try {
            realGet()
        } catch (e: Exception) {
            printError(e.cause.toString())
            null
        }
    }

    /**
     * Initialize the instance and invoke the methods that you called.
     */
    private fun <T> realGet(): T? {
        if (clazz == null && className == null && className!!.isEmpty()) {
            printError("you must specify the className or class.")
            return null
        } else {
            if (clazz == null) {
                this.clazz = Class.forName(className!!)
            }
            if (constructorArgs != null) {
                initInstance()
                invokeMethods()
                return getByResult()
            } else {
                printError("you have to call create() method, or else you will get nothing.")
            }
        }
        return null
    }

    /**
     * Invoke all the methods that you called.
     */
    private fun invokeMethods() {
        if (methodCallList != null && methodCallList!!.size > 0) {
            methodCallList?.forEach {
                invoke(it.key, *it.value)
            }
        }
    }

    /**
     * @Param args: Variable arguments of constructor or method of the class.
     *
     * Get the classes of arguments cof constructor or method of the class.
     */
    private fun getParametersType(args: Array<out Any>): Array<Class<*>?> {
        val result = arrayOfNulls<Class<*>>(args.size)
        for (i in args.indices) {
            result[i] = args[i]::class.java
        }
        return result
    }

    /**
     * @Param args: Variable arguments of constructor or method of the class.
     *
     * Get the classes of arguments cof constructor or method of the class.
     */
    private fun getConversedParametersType(args: Array<out Any>): Array<Class<*>?> {
        val result = arrayOfNulls<Class<*>>(args.size)
        for (i in args.indices) {
            // When the parameters type is int, Kotlin will take it as Integer,
            // so I specify the type as primitive type,
            // I have not found another solution to solve this problem , if you have
            // any idea or suggestion, you can contact me.
            result[i] = when (args[i]) {
                is Byte -> Byte::class.java
                is Short -> Short::class.java
                is Char -> Char::class.java
                is Int -> Int::class.java
                is Long -> Long::class.java
                is Float -> Float::class.java
                is Double -> Double::class.java
                is Boolean -> Boolean::class.java
                else -> args[i]::class.java
            }
        }
        return result
    }


    /**
     *
     * @param name: The name of the method you want to call.
     * @param args: Parameters that use to call the method.
     *
     * Get method by the method name you've passed.
     */
    private fun getMethod(name: String, args: Array<out Any>): Method? {

        var exception: Exception? = null
        var method: Method? = null

        try {
            method = getDeclaredMethod(name, args)
        } catch (e: Exception) {
            exception = e
        }

        if (method == null) {
            try {
                method = getNonDeclaredMethod(name, args)
            } catch (e: Exception) {
                exception = e
            }
        }
        return when {
            method != null -> accessible(method)
            else -> {
                exception?.let {
                    throw it
                }
                return null
            }
        }
    }

    /**
     * Get method.
     */
    private fun getNonDeclaredMethod(name: String, args: Array<out Any>): Method? {
        var exception: Exception? = null
        var method: Method? = null

        try {
            val types = getParametersType(args)
            method = clazz!!.getMethod(name, *types)
        } catch (e: Exception) {
            exception = e
        }

        if (method == null) {
            try {
                val types = getConversedParametersType(args)
                method = clazz!!.getMethod(name, *types)
            } catch (e: Exception) {
                exception = e
            }
        }
        if (method == null) {
            throw exception!!
        }
        return method
    }

    /**
     * Get declared method.
     */
    private fun getDeclaredMethod(name: String, args: Array<out Any>): Method? {
        var exception: Exception? = null
        var declared: Method? = null
        try {
            val types = getParametersType(args)
            declared = clazz!!.getDeclaredMethod(name, *types)
        } catch (e: Exception) {
            exception = e
        }

        if (declared == null) {
            try {
                val types = getConversedParametersType(args)
                declared = clazz!!.getDeclaredMethod(name, *types)
            } catch (e: Exception) {
                exception = e
            }
        }
        if (declared == null) {
            throw exception!!
        } else return declared
    }

    /**
     * Change the accessibility of the methods and constructors.
     */
    private fun <T : AccessibleObject> accessible(accessible: T): T {
        if (accessible is Member) {
            val isPublic = Modifier.isPublic(accessible.modifiers) &&
                    Modifier.isPublic(accessible.declaringClass.modifiers)
            if (isPublic) {
                return accessible
            }
        }

        if (!accessible.isAccessible) {
            accessible.isAccessible = true
        }

        return accessible
    }

    /**
     * Cast to result or instance to the type that you want.
     */
    private fun <T> getByResult(): T? {
        return try {
            if (result != null) {
                result as T
            } else {
                instance as T
            }
        } catch (e: Exception) {
            printError(e.cause.toString())
            null
        }
    }

    companion object {

        /**
         * Set the class name of the instance/
         */
        fun on(className: String): OkReflect {
            return OkReflect(className)
        }

        /**
         * Set the class object of the instance.
         */
        fun on(clazz: Class<String>): OkReflect {
            return OkReflect(clazz)
        }

    }


    /**
     * When exception happen, onError method of this callback will be call
     * if you have specified it.
     */
    interface OkReflectErrorCallback {
        fun onError(errorMsg: String)
    }

}
