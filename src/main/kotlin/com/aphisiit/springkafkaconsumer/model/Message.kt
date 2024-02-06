package com.aphisiit.springkafkaconsumer.model

import com.aphisiit.springkafkaconsumer.utils.json.JSONUtils

data class Message<T>(
	var message: T
) {
	override fun toString(): String {
		return JSONUtils.toJson(this)
	}
}