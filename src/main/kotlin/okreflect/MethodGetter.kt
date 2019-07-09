package okreflect

import okreflect.OkReflect.Companion.accessible
import java.lang.reflect.*

/**
 * This class responsible for getting the method that you want, including constructor.
 */
class MethodGetter {

    companion object {
        /**
         * Get the constructor of the class.
         */
        fun getConstructor(clazz: Class<*>?, constructorArgs: Array<out Any>?): Constructor<out Any>? {
            val declared =
                if (constructorArgs != null && constructorArgs.isNotEmpty()) {
                    val types = getConversedParametersType(constructorArgs)
                    clazz!!.getDeclaredConstructor(*types)
                } else {
                    clazz!!.getDeclaredConstructor()
                }

            return accessible(declared)
        }

        /**
         * @Param args: Variable arguments of constructor or method of the class.
         *
         * Get the classes of arguments cof constructor or method of the class.
         */
        private fun getParametersType(args: Array<out Any?>): Array<Class<*>?> {
            val result = arrayOfNulls<Class<*>>(args.size)
            for (i in args.indices) {
                result[i] = args[i]!!::class.java
            }
            return result
        }

        /**
         * @Param args: Variable arguments of constructor or method of the class.
         *
         * Get the classes of arguments cof constructor or method of the class.
         */
        private fun getConversedParametersType(args: Array<out Any?>): Array<Class<*>?> {
            val result = arrayOfNulls<Class<*>>(args.size)
            for (i in args.indices) {
                // When the parameter type is primitive, Kotlin will box it,
                // so I specify the type as primitive type manually,
                // I have not found another solution to solve this problem , if you have
                // any idea or suggestion, please tell me, thanks.
                result[i] = when (args[i]) {
                    is Byte -> Byte::class.java
                    is Short -> Short::class.java
                    is Char -> Char::class.java
                    is Int -> Int::class.java
                    is Long -> Long::class.java
                    is Float -> Float::class.java
                    is Double -> Double::class.java
                    is Boolean -> Boolean::class.java
                    else -> args[i]!!::class.java
                }
            }
            return result
        }

        /**
         * @param clazz: The class that you want to use.
         * @param name: The name of the method you want to call.
         * @param args: Parameters that use to call the method.
         * @param parameterTypes: The class of parameters in the method.
         *
         * Get method by the name and parameters of the method  you've passed.
         */
        fun getMethod(
            clazz: Class<*>?,
            name: String,
            args: Array<out Any?>,
            parameterTypes: Array<Class<*>>?
        ): Method? {

            var exception: Exception? = null
            var method: Method? = null

            try {
                method = getDeclaredMethod(clazz, name, args, parameterTypes)
            } catch (e: Exception) {
                exception = e
            }

            if (method == null) {
                try {
                    method = getNonDeclaredMethod(clazz, name, args, parameterTypes)
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
        private fun getNonDeclaredMethod(
            clazz: Class<*>?,
            name: String,
            args: Array<out Any?>,
            parameterTypes: Array<Class<*>>?
        ): Method? {
            var exception: Exception? = null
            var method: Method? = null

            try {
                val types = parameterTypes ?: getParametersType(args)
                method = clazz!!.getMethod(name, *types)
            } catch (e: Exception) {
                exception = e
            }

            if (method == null) {
                try {
                    val types = parameterTypes ?: getConversedParametersType(args)
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
        private fun getDeclaredMethod(
            clazz: Class<*>?,
            name: String,
            args: Array<out Any?>,
            parameterTypes: Array<Class<*>>?
        ): Method? {
            var exception: Exception? = null
            var declared: Method? = null
            try {
                val types = parameterTypes ?: getParametersType(args)
                declared = clazz!!.getDeclaredMethod(name, *types)
            } catch (e: Exception) {
                exception = e
            }

            if (declared == null) {
                try {
                    val types = parameterTypes ?: getConversedParametersType(args)
                    declared = clazz!!.getDeclaredMethod(name, *types)
                } catch (e: Exception) {
                    exception = e
                }
            }
            if (declared == null) {
                throw exception!!
            } else return declared
        }

    }








}
