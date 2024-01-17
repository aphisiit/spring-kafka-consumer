package com.aphisiit.springkafkaconsumer.common

data class Foo2 (
	var foo: String?
){
	constructor(): this(null)

	override fun toString(): String = "Foo2 [foo=$foo]"
}
