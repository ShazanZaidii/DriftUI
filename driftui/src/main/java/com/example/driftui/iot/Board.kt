package com.example.driftui.iot

import android.util.Base64
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class PinState { HIGH, LOW }

object Board {
    private const val BROKER = "tcp://broker.emqx.io:1883"
    private var mqttClient: MqttClient? = null

    private var secretKey: String = ""
    private var targetTopic: String = ""

    fun init(boardKey: String) {
        System.setProperty("java.net.preferIPv4Stack", "true")
        secretKey = boardKey
        targetTopic = "driftui/boards/${hashTopic(secretKey)}/rx"
        println("DriftUI: Locked onto secure topic -> $targetTopic")
    }

    private fun hashTopic(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        val hexString = StringBuilder()
        for (i in 0 until 16) {
            val hex = Integer.toHexString(0xff and bytes[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    private fun encryptPayload(plainText: String): String {
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    @Synchronized
    private fun ensureConnection() {
        if (mqttClient == null || mqttClient?.isConnected == false) {
            try {
                val clientId = MqttClient.generateClientId()
                mqttClient = MqttClient(BROKER, clientId, MemoryPersistence())

                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    connectionTimeout = 10
                    isAutomaticReconnect = true
                }

                mqttClient?.connect(options)
                println("DriftUI: Secure network link established via Mosquitto.")
            } catch (e: Exception) {
                println("DriftUI Connection Failed: ${e.message}")
            }
        }
    }

    // --- HYBRID API (Imperative Command + Declarative State) ---

    fun digitalWrite(pin: Int, state: PinState = PinState.LOW): MutableState<PinState> {
        val initialPayload = JSONObject().apply {
            put("action", "digitalWrite")
            put("pin", pin)
            put("state", state.name)
        }
        publish(initialPayload.toString())

        val internalState = mutableStateOf(state)
        return object : MutableState<PinState> by internalState {
            override var value: PinState
                get() = internalState.value
                set(newValue) {
                    if (internalState.value != newValue) {
                        internalState.value = newValue
                        val updatePayload = JSONObject().apply {
                            put("action", "digitalWrite")
                            put("pin", pin)
                            put("state", newValue.name)
                        }
                        publish(updatePayload.toString())
                    }
                }
        }
    }

    fun analogWrite(pin: Int, value: Int = 0): MutableState<Int> {
        val safeInitial = value.coerceIn(0, 255)
        val initialPayload = JSONObject().apply {
            put("action", "pwm")
            put("pin", pin)
            put("value", safeInitial)
        }
        publish(initialPayload.toString())

        val internalState = mutableStateOf(safeInitial)
        return object : MutableState<Int> by internalState {
            override var value: Int
                get() = internalState.value
                set(newValue) {
                    val safeUpdate = newValue.coerceIn(0, 255)
                    if (internalState.value != safeUpdate) {
                        internalState.value = safeUpdate
                        val updatePayload = JSONObject().apply {
                            put("action", "pwm")
                            put("pin", pin)
                            put("value", safeUpdate)
                        }
                        publish(updatePayload.toString())
                    }
                }
        }
    }

    fun servoWrite(pin: Int, angle: Int = 0): MutableState<Int> {
        val safeAngle = angle.coerceIn(0, 180)
        val initialPayload = JSONObject().apply {
            put("action", "servo")
            put("pin", pin)
            put("angle", safeAngle)
        }
        publish(initialPayload.toString())

        val internalState = mutableStateOf(safeAngle)
        return object : MutableState<Int> by internalState {
            override var value: Int
                get() = internalState.value
                set(newValue) {
                    val angleUpdate = newValue.coerceIn(0, 180)
                    if (internalState.value != angleUpdate) {
                        internalState.value = angleUpdate
                        val updatePayload = JSONObject().apply {
                            put("action", "servo")
                            put("pin", pin)
                            put("angle", angleUpdate)
                        }
                        publish(updatePayload.toString())
                    }
                }
        }
    }

    // NEW: Flushes the hardware task queue for a specific pin
    fun flush(pin: Int) {
        val payload = JSONObject().apply {
            put("action", "flush")
            put("pin", pin)
        }
        publish(payload.toString())
    }

    private fun publish(jsonPayload: String) {
        if (secretKey.isEmpty()) {
            println("DriftUI ERROR: Board not initialized. Call Board.init() first.")
            return
        }

        Thread {
            try {
                ensureConnection()
                val encryptedMessage = encryptPayload(jsonPayload)
                val message = MqttMessage(encryptedMessage.toByteArray())
                message.qos = 0
                mqttClient?.publish(targetTopic, message)
            } catch (e: Exception) {
                println("DriftUI Engine Error: ${e.message}")
            }
        }.start()
    }

    // --- SYNCHRONIZED EXECUTION BLOCK ---

    class SyncBlock {
        val operations = org.json.JSONArray()

        fun digitalWrite(pin: Int, state: PinState) {
            val op = JSONObject().apply {
                put("action", "digitalWrite")
                put("pin", pin)
                put("state", state.name)
            }
            operations.put(op)
        }

        fun analogWrite(pin: Int, value: Int) {
            val op = JSONObject().apply {
                put("action", "pwm")
                put("pin", pin)
                put("value", value.coerceIn(0, 255))
            }
            operations.put(op)
        }

        fun servoWrite(pin: Int, angle: Int) {
            val op = JSONObject().apply {
                put("action", "servo")
                put("pin", pin)
                put("angle", angle.coerceIn(0, 180))
            }
            operations.put(op)
        }

        fun delay(duration: Int) {
            val op = JSONObject().apply {
                put("action", "delay")
                put("duration", duration)
            }
            operations.put(op)
        }
    }

    fun sync(block: SyncBlock.() -> Unit) {
        val syncBlock = SyncBlock()
        syncBlock.block()

        val payload = JSONObject().apply {
            put("batch", syncBlock.operations)
        }
        publish(payload.toString())
    }
}