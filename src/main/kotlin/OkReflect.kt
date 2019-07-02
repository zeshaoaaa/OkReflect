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
    private var methodCallList: ArrayList<MethodCall>? = null

    /**
     * All the fields that you want to set will store with this map.
     */
    private var setFieldMap: LinkedHashMap<String, Any>? = null

    /**
     * The name of the field that you want to get.
     */
    private var targetFieldName: String? = null

    /**
     * The value fo the field that you want to get.
     */
    private var targetFieldValue: Any? = null

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
    fun error(action: (java.lang.Exception) -> Unit): OkReflect {
        errorCallback = object : OkReflectErrorCallback {
            override fun onError(e: Exception) {
                action(e)
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
     * The method will be called when [get] method called.
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
     * The method will be called when [get] method called.
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
    private fun realCall(
        methodName: String, callWithInstance: Boolean = true,
        vararg args: Any
    ): OkReflect {
        if (methodCallList == null) {
            methodCallList = ArrayList()
        }
        methodCallList!!.add(MethodCall(methodName, callWithInstance, args))
        return this
    }

    /**
     * @param fieldName: The name of the field that you want to set.
     * @param arg: The value of the field that you want to set.
     */
    fun set(fieldName: String, arg: Any): OkReflect {
        if (setFieldMap == null) {
            setFieldMap = LinkedHashMap()
        }
        setFieldMap!![fieldName] = arg
        return this
    }


    /**
     * @param methodCall: This object include the name of the method, whether
     * call the method with instance or not, and parameters for invoking the method.
     *
     * Invoke the the method that you called by call() method.
     */
    private fun invoke(methodCall: MethodCall) {
        val args = methodCall.args
        val method = getMethod(methodCall.methodName, args)
        val withInstance = methodCall.callWithInstance
        val returnType = method!!.returnType.toString()
        if (returnType == "void") {
            method.invoke(instance, *args)
        } else {
            result = if (withInstance) {
                method.invoke(instance, *args)
            } else {
                if (result != null) {
                    method.invoke(result, *args)
                } else {
                    throw java.lang.NullPointerException(
                        "you cannot call the method " +
                                "with the return value of last method when it's null."
                    )
                }
            }
        }

    }

    /**
     * @param fieldName: The name of the field.
     * @param arg: The value that you want to set to the field.
     */
    private fun setField(fieldName: String, arg: Any) {
        val field = instance!!.javaClass.getDeclaredField(fieldName)
        accessible(field).set(instance, arg)
    }

    /**
     * Initialize the target field value.
     */
    private fun initFieldValue() {
        if (targetFieldName != null) {
            val field = instance!!.javaClass.getDeclaredField(targetFieldName)
            targetFieldValue = accessible(field).get(instance)
        }
    }

    /**
     * @param proxyClass:The proxy object
     */
    fun <T> use(proxyClass: Class<T>): T {
        val handler = object : InvocationHandler {
            override fun invoke(proxy: Any?, method: Method?, vararg args: Any): Any {
                var methodName: String? = null
                if (method != null) {
                    methodName = method.name
                }
                if (methodName != null) {
                    try {
                        return call(methodName, *args).get()!!
                    } catch (e: Exception) {
                        throw e
                    }
                } else {
                    throw Exception("Cannot find the method tha you invoked.")
                }
            }
        }
        return Proxy.newProxyInstance(proxyClass.classLoader, arrayOf(proxyClass), handler) as T
    }

    /**
     * @param msg: The error message that you want to output.
     *
     * Print the exception message.
     * If you have already pass the ErrorCallback into onError method,
     * you will receive the error message from callback.
     */
    private fun printError(e: java.lang.Exception) {
        if (errorCallback != null) {
            errorCallback!!.onError(e)
        } else {
            throw e
        }
    }

    /**
     * Get the result value from last method if it has a return value,
     * or else you will get the instance.
     */
    fun <T> get(): T? {
        return try {
            realGet(RETURN_FLAG_RESULT)
        } catch (e: Exception) {
            printError(e)
            null
        }
    }

    /**
     * Get the instance no matter result have value or not.
     */
    fun <T> getInstance(): T? {
        return getByFlag<T>(RETURN_FLAG_INSTANCE)

    }

    /**
     * Get the field from the instance.
     */
    fun <T> getField(fieldName: String): T? {
        targetFieldName = fieldName
        return getByFlag<T>(RETURN_FLAG_FIELD)
    }

    /**
     * Return value by flag.
     */
    private fun <T> getByFlag(returnFlag: Int): T? {
        return try {
            realGet(returnFlag)
        } catch (e: Exception) {
            printError(e)
            null
        }
    }

    /**
     * Initialize the instance and invoke the methods that you called.
     */
    private fun <T> realGet(returnFlag: Int): T? {
        return if (clazz == null && className == null && className!!.isEmpty()) {
            throw java.lang.NullPointerException("you must specify the className or class.")
        } else {
            if (clazz == null) {
                this.clazz = Class.forName(className!!)
            }
            if (constructorArgs != null) {
                initInstance()
                invokeMethods()
                setFields()
                initFieldValue()
                getByResult<T>(returnFlag)
            } else {
                throw NullPointerException("you have to call create() method, or else you will get nothing.")
            }
        }
    }

    /**
     * Invoke all the methods that you called.
     */
    private fun invokeMethods() {
        if (methodCallList != null && methodCallList!!.size > 0) {
            methodCallList?.forEach {
                invoke(it)
            }
        }
    }

    private fun setFields() {
        if (setFieldMap != null && setFieldMap!!.size > 0) {
            setFieldMap?.forEach {
                setField(it.key, it.value)
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
    private fun <T> getByResult(returnFlag: Int): T? {
        return try {
            when (returnFlag) {
                RETURN_FLAG_RESULT -> {
                    if (result != null) {
                        result as T
                    } else {
                        instance as T
                    }
                }
                RETURN_FLAG_INSTANCE -> instance as T
                else -> targetFieldValue as T
            }
        } catch (e: Exception) {
            printError(e)
            null
        }
    }

    companion object {

        /**
         * Return the return value from the method that you invoked.
         */
        private const val RETURN_FLAG_RESULT = 1

        /**
         * Return the instance.
         */
        private const val RETURN_FLAG_INSTANCE = 2

        /**
         * Return the field.
         */
        private const val RETURN_FLAG_FIELD = 3

        /**
         * Set the class name of the instance/
         */
        @JvmStatic
        fun on(className: String): OkReflect {
            return OkReflect(className)
        }

        /**
         * Set the class object of the instance.
         */
        @JvmStatic
        fun on(clazz: Class<String>): OkReflect {
            return OkReflect(clazz)
        }

        /**
         * @param path: The path and name of the class.
         * @param content: The content of the class.
         * @param options: The options use for compile.
         *
         * Compile content to class.
         */
        @JvmStatic
        fun compile(path: String, content: String, options: OkCompileOptions) {
            OkCompiler.compile(path, content, options)
        }

        /**
         * @see [compile]
         */
        @JvmStatic
        fun compile(path: String, content: String) {
            compile(path, content, OkCompileOptions())
        }


    }

    /**
     * When exception happen, onError method of this callback will be call
     * if you have specified it.
     */
    interface OkReflectErrorCallback {
        fun onError(e: Exception)
    }

    class MethodCall(val methodName: String, val callWithInstance: Boolean, val args: Array<out Any>)

}
