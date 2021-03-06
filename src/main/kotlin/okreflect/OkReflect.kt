package okreflect

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okreflect.MethodGetter.Companion.accessible
import okreflect.MethodGetter.Companion.getConstructor
import okreflect.MethodGetter.Companion.getMethod
import java.lang.reflect.*

/**
 * OkReflect is a library that trying to help you use Java Reflect feature with ease.
 *
 * The reasons why I develop this library is because I run into some problem while using jOOR.
 * If you encounter some problem or you have some suggestion for me, you can post an issue,
 * I will solve it for you as soon as possible.
 *
 * @author <a href="https://github.com/zeshaoaaa">zeshaoaaa</a>
 *
 */
open class OkReflect {


    /**
     * The className of the object that you want to create.
     */
    private var className: String? = null

    /**
     * The constructor parameters of the object that you want to create.
     */
    private var constructorArgs: Array<out Any>? = null

    /**
     * Indicate that create() method called or not.
     */
    private var createCalled = false

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
     * OnError method of this async will be call when exception happen.
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
     * Whether call methods with outer instance or not.
     */
    private var withOuterInstance = false

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
    constructor(clazz: Class<*>) {
        this.clazz = clazz
    }

    /**
     * @param instance: The instance that you want to use for calling methods.
     *
     * Constructor of OkReflect.
     */
    constructor(instance: Any, isInstance: Boolean = true) {
        withOuterInstance = isInstance
        this.instance = instance
        this.clazz = instance.javaClass
    }

    /**
     * Set the parameters of the constructor of the class that you want to create.
     */
    fun create(vararg args: Any): OkReflect {
        this.createCalled = true
        this.constructorArgs = args
        return this
    }

    /**
     * Initialized the instance.
     */
    private fun initInstance() {
        val constructor = getConstructor(clazz, constructorArgs)
        val instance = if (constructorArgs == null) {
            constructor!!.newInstance()
        } else {
            constructor!!.newInstance(*constructorArgs!!)
        }
        this.instance = instance
    }

    /**
     * @param action: The action that you want to take when exception happen.
     *
     * Set the error async for receive error message when exception happen.
     * If you have not set the async, then you have catch exception by yourself.
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
     * @param okReflectErrorCallback: Exception async.
     *
     * Set the error async for receive error message when exception happen.
     * If you have not set the async, then you have catch exception by yourself.
     */
    fun error(okReflectErrorCallback: OkReflectErrorCallback): OkReflect {
        this.errorCallback = okReflectErrorCallback
        return this
    }

    /**
     * @param methodName: The name of the method that you want to call.
     * @param classes: The class of the parameters.
     * @param args: The parameters of the method that you wan to call.
     *
     * Call the method that you want to call.
     * The method will be called when [get] method called.
     * The method will be call with the instance.
     */
    fun callWithClass(methodName: String, classes: Array<Class<*>>?, vararg args: Any?): OkReflect {
        return realCall(true, methodName, classes, *args)
    }

    /**
     * @See [call]
     */
    fun call(methodName: String, vararg args: Any): OkReflect {
        return callWithClass(methodName, null, *args)
    }

    /**
     * @param methodName: The name of the method that you want to call.
     * @param classes: The class of the parameters.
     * @param args: The parameters of the method that you wan to call.
     *
     * Call the method and return the return value of the method directly.
     */
    fun <T> simpleCall(methodName: String, classes: Array<Class<*>>?, vararg args: Any): T? {
        realCall(true, methodName, classes, *args)
        return get()
    }

    /**
     * @See [simpleCall]
     */
    fun <T> simpleCall(methodName: String, vararg args: Any): T? {
        return simpleCall<T>(methodName, null, *args)
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
        return realCall(false, methodName, null, *args)
    }

    /**
     * @param methodName: The name of the method that you want to call.
     * @param callWithInstance: You can call the method with instance or with the return value from last method.
     * @param args: The parameters of the method that you wan to call.
     *
     * Invoke the method with instance or the return type from last method.
     */
    private fun realCall(
        callWithInstance: Boolean = true,
        methodName: String,
        classes: Array<Class<*>>?,
        vararg args: Any?
    ): OkReflect {
        if (methodCallList == null) {
            methodCallList = ArrayList()
        }
        val call = MethodCall(methodName, classes, callWithInstance, args)
        methodCallList!!.add(call)
        return this
    }

    /**
     * @param instance: The instance that you want to get field value for.
     *
     * When you want to get or set field of super class from a specific instance,
     * you need to pass the instance in this method.
     */
    fun with(instance: Any): OkReflect {
        withOuterInstance = true
        this.instance = instance
        return this
    }

    /**
     * @param fieldName: The name of the field that you want to set.
     * @param arg: The value of the field that you want to set.
     *
     * Set field value.
     */
    fun set(fieldName: String, arg: Any): OkReflect {
        addArgToFieldMap(fieldName, arg)
        return this
    }

    /**
     * @param fieldName: The name of the field that you want to set.
     * @param arg: The value of the field that you want to set.
     *
     * Add parameter to field map for use it later.
     */
    private fun addArgToFieldMap(fieldName: String, arg: Any) {
        if (setFieldMap == null) {
            setFieldMap = LinkedHashMap()
        }
        setFieldMap!![fieldName] = arg
    }

    /**
     * @param fieldName: The name of the field that you want to set.
     * @param arg: The value of the field that you want to set.

     * Set field value and return the value.
     */
    fun <T> simpleSet(fieldName: String, arg: Any): T? {
        addArgToFieldMap(fieldName, arg)
        return get(fieldName)
    }


    /**
     * @param fieldName: The name of the field.
     * @param arg: The value that you want to set to the field.
     */
    private fun setField(fieldName: String, arg: Any) {
        if (instance != null) {
            setFieldOfInstance(fieldName, arg)
        } else {
            var field = clazz!!.getDeclaredField(fieldName)
            field = accessible(field)
            val fieldObj = field.get(null)
            accessible(field).set(fieldObj, arg)
        }
    }

    /**
     * @param fieldName: The name of the field that you want to set.
     * @param arg: The value of the field that you want to sef.
     *
     * Set the field of the instance.
     */
    private fun setFieldOfInstance(fieldName: String, arg: Any) {
        val osName = System.getProperty("os.name")
        var field = clazz!!.getDeclaredField(fieldName)
        field = accessible(field)
        if (osName != "Linux") {
            modifyModifiers(field)
        }
        field.set(instance, arg)
    }

    /**
     * @param field The field that you want to modify.
     *
     * Modify the field modifiers of the final field for the purpose of make it modifiable.
     */
    private fun modifyModifiers(field: Field) {
        var modifiers = Field::class.java.getDeclaredField("modifiers")
        modifiers = accessible(modifiers)
        modifiers.setInt(field, field.modifiers and Modifier.FINAL.inv())
    }

    /**
     * @param methodCall: This object include the name of the method, whether
     * call the method with instance or not, and parameters for invoking the method.
     *
     * Invoke the the method that you called by call() method.
     */
    private fun invoke(methodCall: MethodCall) {
        val args = methodCall.args
        val method = getMethod(clazz, methodCall.classes, methodCall.methodName, args)
        val returnType = method!!.returnType.toString()
        if (returnType == "void") {
            method.invoke(instance, *args)
        } else {
            result = if (methodCall.callWithInstance) {
                method.invoke(instance, *args)
            } else {
                verifyResult()
                method.invoke(result, *args)
            }
        }

    }

    /**
     * When calling method with result, then the result must exists.
     */
    private fun verifyResult() {
        if (result == null) {
            throw java.lang.NullPointerException(
                "you cannot call the method with the return value of last method when it's null."
            )
        }
    }

    /**
     * Initialize the field field value.
     */
    private fun initFieldValue() {
        targetFieldValue = if (targetFieldName != null) {
            var exception: Throwable? = null
            var value = try {
                initFieldValueFromDeclaredField()
            } catch (e: java.lang.Exception) {
                exception = e
                null
            }
            if (value == null) {
                value = try {
                    initFieldValueFromField()
                } catch (e: java.lang.Exception) {
                    exception = e
                    null
                }
            }
            value ?: throw exception!!
        } else {
            null
        }
    }

    /**
     * Set value of the field from the declared field.
     */
    private fun initFieldValueFromDeclaredField(): Any? {
        return if (instance == null) {
            val field = clazz!!.getDeclaredField(targetFieldName)
            accessible(field).get(null)
        } else {
            val field = clazz!!.getDeclaredField(targetFieldName)
            accessible(field).get(instance)
        }
    }

    private fun initFieldValueFromField(): Any? {
        return if (instance == null) {
            val field = clazz!!.getField(targetFieldName)
            accessible(field).get(null)
        } else {
            val field = clazz!!.getField(targetFieldName)
            accessible(field).get(instance)
        }
    }

    /**
     * @param proxyClass:The class of proxy object.
     *
     * Create instance from this class.
     */
    fun <T> use(proxyClass: Class<T>): T {
        val handler = InvocationHandler { proxy, method, args ->
            var methodName: String? = null
            if (method != null) {
                methodName = method.name
                try {
                    call(methodName, *args).get<T>()!!
                } catch (e: Exception) {
                    printError(e)
                    null
                }
            } else {
                printError(Exception("Cannot find the method tha you invoked."))
            }
        }
        return Proxy.newProxyInstance(proxyClass.classLoader, arrayOf(proxyClass), handler) as T
    }

    /**
     * @param e: The exception that you want to output.
     *
     * Print the exception message.
     * If you have already pass the ErrorCallback into onError method,
     * you will receive the error message from async.
     */
    private fun printError(e: java.lang.Exception) {
        if (errorCallback != null) {
            errorCallback!!.onError(e)
        } else {
            throw e
        }
    }

    /**
     * @param fieldName: The name of the field that you want to get.
     *
     * Get instance when return value from last method is null.
     */
    fun <T> get(fieldName: String): T? {
        targetFieldName = fieldName
        return getByFlag<T>(RETURN_FLAG_FIELD)
    }

    /**
     * @param fieldName: The name of the field that you want to get.
     *
     * Get instance when return value from last method is null.
     */
    fun field(fieldName: String? = null): OkReflect {
        targetFieldName = fieldName
        return this
    }

    /**
     * @param asyncCallback: The async the you can handle the result in it.
     *
     * When you trying to execute the reflection operation asynchronously,
     * you can set the async for handling the result.
     */
    fun <T> async(asyncCallback: OkReflectAsyncCallback) {
        launchReflection<T> {
            asyncCallback.onResult(it)
        }
    }

    /**
     * @param action: The action you want to take after task finished.
     *
     * When you trying to execute the reflection operation asynchronously,
     * you can set the actionfor handling the result.
     */
    fun <T> callback(action: (result: T?) -> Unit) {
        launchReflection<T>(action)
    }

    /**
     * Launch reflection task.
     */
    private fun <T> launchReflection(action: (result: T?) -> Unit) {
        val flag = if (targetFieldName == null) RETURN_FLAG_RESULT_OR_INSTANCE else RETURN_FLAG_FIELD
        GlobalScope.launch {
            val result = realGet<T>(flag)
            action(result)
        }
    }


    /**
     * @see [get]
     */
    fun <T> get(): T? {
        return getByFlag(RETURN_FLAG_RESULT_OR_INSTANCE)
    }

    /**
     * OkReflect will return instance when the result is null,
     * when you trying to get return value no matter result is null,
     * then you can use this method.
     */
    fun <T> getResult(): T? {
        return getByFlag(RETURN_FLAG_RESULT)
    }

    /**
     * Get the class.
     */
    fun getClazz(): Class<*>? {
        return getByFlag(RETURN_FLAG_CLASS)
    }

    /**
     * Get the instance no matter result have value or not.
     */
    fun <T> getInstance(): T? {
        return getByFlag<T>(RETURN_FLAG_INSTANCE)
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
     * Initialize the instance, invoke the methods that you called
     * and set or get the field that you want.
     */
    private fun <T> realGet(returnFlag: Int): T? {
        if (!withOuterInstance) {
            if (needInstance(returnFlag)) {
                verifyClassInfo()
                verifyConstructorArgs()
            }
            initClazz()
            if (createCalled) {
                initInstance()
                invokeMethods()
            }
        } else {
            invokeMethods()
        }
        setFields()
        initFieldValue()
        return getByResult<T>(returnFlag)
    }

    /**
     * Initialize field class by className.
     */
    private fun initClazz() {
        if (clazz == null) {
            this.clazz = Class.forName(className!!)
        }
    }

    /**
     * When flag is RETURN_FLAG_INSTANCE or RETURN_FLAT_RESULT_OR_INSTANCE,
     * it means following operation needs instance.
     */
    private fun needInstance(returnFlag: Int): Boolean {
        return returnFlag == RETURN_FLAG_INSTANCE || returnFlag == RETURN_FLAG_RESULT_OR_INSTANCE
    }

    /**
     * If there is no constructor parameters for the , there will throw an exception
     */
    private fun verifyConstructorArgs() {
        if (constructorArgs == null) {
            throw NullPointerException(
                "you have to call create() method, or else you will get nothing."
            )
        }
    }

    /**
     * If there is no class info, there will throw an exception
     */
    private fun verifyClassInfo() {
        if (clazz == null && className == null && className!!.isEmpty()) {
            throw java.lang.NullPointerException(
                "you must specify the className or class."
            )
        }
    }

    /**
     * Invoke all the methods that you called.
     */
    private fun invokeMethods() {
        methodCallList?.forEach {
            invoke(it)
        }
    }

    /**
     * Set all the fields that you want to set.
     */
    private fun setFields() {
        if (setFieldMap != null && setFieldMap!!.size > 0) {
            setFieldMap?.forEach {
                setField(it.key, it.value)
            }
        }
    }

    /**
     * Cast to result or instance to the type that you want.
     */
    private fun <T> getByResult(returnFlag: Int): T? {
        return try {
            realGetByFlag(returnFlag)
        } catch (e: Exception) {
            printError(e)
            null
        }
    }

    private fun <T> realGetByFlag(returnFlag: Int): T? {
        return when (returnFlag) {
            RETURN_FLAG_FIELD -> {
                targetFieldValue as T
            }
            RETURN_FLAG_RESULT -> result as T
            RETURN_FLAG_RESULT_OR_INSTANCE -> {
                if (result != null) {
                    result as T
                } else {
                    instance as T
                }
            }
            RETURN_FLAG_INSTANCE -> instance as T
            else -> clazz as T
        }
    }

    /**
     * Reset all the data.
     */
    private fun reset(clazz: Class<*>) {
        this.clazz = clazz
        this.className = null
        this.instance = null
        this.withOuterInstance = false


        clearCommonData()
    }

    /**
     * Reset all the data.
     */
    private fun reset(className: String) {
        this.clazz = null
        this.className = className
        this.instance = null
        this.withOuterInstance = false

        clearCommonData()
    }

    /**
     * Reset all the data.
     */
    private fun reset(instance: Any, isInstance: Boolean = true) {
        this.clazz = instance.javaClass
        this.className = null
        this.instance = instance
        this.withOuterInstance = true

        clearCommonData()
    }

    private fun clearCommonData() {
        this.constructorArgs = null
        this.createCalled = false
        this.result = null
        this.errorCallback = null
        this.targetFieldName = null
        this.targetFieldValue = null
        this.setFieldMap?.clear()
        this.methodCallList?.clear()
    }

    companion object {

        /**
         * The instance of OkReflect.
         */
        private var instance: OkReflect? = null

        /**
         * Return the class.
         */
        private const val RETURN_FLAG_CLASS = 1

        /**
         * Return the instance.
         */
        private const val RETURN_FLAG_INSTANCE = 2

        /**
         * Return the return value from the method that you invoked.
         */
        private const val RETURN_FLAG_RESULT_OR_INSTANCE = 3

        /**
         * Return the field.
         */
        private const val RETURN_FLAG_FIELD = 4

        /**
         * Return the return value from the invoked method.
         */
        private const val RETURN_FLAG_RESULT = 5

        /**
         * Set the class name of the instance
         */
        @JvmStatic
        fun on(className: String): OkReflect {
            if (instance == null) {
                instance = OkReflect(className)
            } else {
                this.instance!!.reset(className)
            }
            return instance!!
        }

        /**
         * Set the class object of the instance.
         */
        @JvmStatic
        fun on(clazz: Class<*>): OkReflect {
            if (instance == null) {
                instance = OkReflect(clazz)
            } else {
                this.instance!!.reset(clazz)
            }
            return instance!!
        }

        /**
         * @param instance: The instance that you want to use.
         *
         * Set the instance for methods that you want to call.
         */
        @JvmStatic
        fun onInstance(instance: Any): OkReflect {
            if (this.instance == null) {
                this.instance = OkReflect(instance, true)
            } else {
                this.instance!!.reset(instance, true)
            }
            return this.instance!!
        }

    }

    /**
     * When exception happen, the onError() method will be called
     * if you have specified it.
     */
    interface OkReflectErrorCallback {
        fun onError(e: Exception)
    }

    /**
     * When the reflection task finished, onResult() will be called if you have set it.
     */
    interface OkReflectAsyncCallback {
        fun onResult(result: Any?)
    }

}

