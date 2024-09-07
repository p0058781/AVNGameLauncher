package org.skynetsoftware.avnlauncher.domain.model.filter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class FilterQuery(
    val property: PropertyType,
    val condition: Condition,
    @Serializable(with = ValueSerializer::class)
    val value: Value,
    val compoundCondition: CompoundCondition? = null,
    val compoundOtherFilterQuery: FilterQuery? = null,
)

@Serializable
data class Property(
    val type: PropertyType,
    val name: String,
    val nullable: Boolean,
)

@Serializable
sealed class Value {
    data object Null: Value()
    data class Literal(val value: Any): Value()
    data class Reference(val property: Property): Value()
}

@Serializable
sealed class PropertyType(
    val type: kotlin.String,
    val supportedConditions: Array<Condition>,
) {
    data object String: PropertyType(kotlin.String::class.java.name, Condition.entries.toTypedArray())
    data object Number: PropertyType(kotlin.Number::class.java.name, Condition.entries.toTypedArray())
    data object Boolean: PropertyType(kotlin.Boolean::class.java.name, arrayOf(Condition.Eq, Condition.Neq))
}

enum class Condition {
    Eq,
    Neq,
    Gt,
    Lt,
    Get,
    Let,
    ;
}

enum class CompoundCondition {
    And,
    Or,
    ;
}

object ValueSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("value", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        //encoder.encodeString(value.name)
        throw RuntimeException("not implemented")
    }

    override fun deserialize(decoder: Decoder): PropertyType {
        //return Class.forName(decoder.decodeString())
        throw RuntimeException("not implemented")
    }
}

