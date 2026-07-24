package de.florianisme.wakeonlan.ui.modify

import androidx.annotation.StringRes
import de.florianisme.wakeonlan.R

/**
 * Pure validation helpers that replace the old TextWatcher-based Validator classes.
 * Each function returns a string resource id for the error, or null when the input is valid.
 */
object DeviceFormValidation {

    private val MAC_PATTERN = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    private val IP_PATTERN = Regex("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")

    @StringRes
    fun validateMac(text: String): Int? =
        if (MAC_PATTERN.matches(text.trim())) null else R.string.add_device_error_mac_invalid

    @StringRes
    fun validatePort(port: String): Int? {
        if (port.isEmpty()) return null
        val parsed = port.toIntOrNull() ?: return R.string.add_device_error_port_invalid
        return if (parsed in 1..65535) null else R.string.add_device_error_port_invalid
    }

    private fun isValidIp(text: String, optional: Boolean): Boolean {
        if (optional && text.trim().isEmpty()) return true
        return IP_PATTERN.matches(text.trim())
    }

    @StringRes
    fun validateSecureOnPassword(text: String): Int? {
        val length = text.toByteArray(Charsets.US_ASCII).size
        val valid =
            length == 0 || isValidIp(text, optional = false) || MAC_PATTERN.matches(text.trim())
        return if (valid) null else R.string.add_device_error_secure_on_password_invalid
    }

    @StringRes
    fun validateNotEmpty(text: String, @StringRes errorRes: Int): Int? =
        if (text.isEmpty()) errorRes else null

    @StringRes
    fun validateConditionalNotEmpty(
        text: String,
        shouldValidate: Boolean,
        @StringRes errorRes: Int
    ): Int? {
        if (!shouldValidate) return null
        return if (text.isEmpty()) errorRes else null
    }

    /**
     * Mirrors [MacAddressAutocomplete]: auto-inserts colons while the user types a MAC address.
     */
    fun autoFormatMac(previous: String, current: String): String {
        val isDeleting = current.length < previous.length
        if (isDeleting) return current
        if (current.length >= 17 || current.endsWith(":")) return current

        val groups = current.split(":")
        if (groups.isEmpty()) return current
        val last = groups.last()
        return when (last.length) {
            3 -> current.substring(
                0,
                current.length - 1
            ) + ":" + current.substring(current.length - 1)

            2 -> "$current:"
            else -> current
        }
    }
}

