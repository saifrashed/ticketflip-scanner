package com.ticketflip.scanner.util

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 * Created by bradhawk on 1/29/2016.
 */
class PrimitiveConverterFactory private constructor() : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type === String::class.java) {
            return Converter { value -> value.string() }
        } else if (type === Int::class.java) {
            return Converter { value -> Integer.valueOf(value.string()) }
        } else if (type === Double::class.java) {
            return Converter { value -> java.lang.Double.valueOf(value.string()) }
        } else if (type === Boolean::class.java) {
            return Converter { value -> java.lang.Boolean.valueOf(value.string()) }
        }
        return null
    }

    companion object {
        fun create(): PrimitiveConverterFactory {
            return PrimitiveConverterFactory()
        }
    }
}