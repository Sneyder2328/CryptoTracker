package com.sneyder.cryptotracker.data.api

import debug
import retrofit2.Converter
import retrofit2.Retrofit
import okhttp3.ResponseBody
import java.lang.reflect.Type


class AnnotatedConverterFactory(private val factoryMap: Map<Class<*>, Converter.Factory>): Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            val factory = factoryMap[annotation.annotationClass.java]
            debug("annotation factory = ${factory.toString()}")
            if(factory!=null) return factory.responseBodyConverter(type, annotations, retrofit)
        }
        debug("default to json in case no annotation on current method was found")
        //try to default to json in case no annotation on current method was found
        val jsonFactory = factoryMap[Json::class.java]
        return jsonFactory?.responseBodyConverter(type, annotations, retrofit)
    }


    internal class Builder {
        private var factoryMap: MutableMap<Class<*>, Converter.Factory> = LinkedHashMap()

        fun add(factoryType: Class<out Annotation>?, factory: Converter.Factory?): Builder {
            if (factoryType == null) throw NullPointerException("factoryType is null")
            if (factory == null) throw NullPointerException("factory is null")
            factoryMap[factoryType] = factory
            return this
        }

        fun build(): AnnotatedConverterFactory {
            return AnnotatedConverterFactory(factoryMap)
        }

    }
}

@Retention(AnnotationRetention.RUNTIME)
annotation class Json