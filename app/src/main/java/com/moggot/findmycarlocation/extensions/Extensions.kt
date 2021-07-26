package com.moggot.findmycarlocation.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.moggot.findmycarlocation.R

fun Fragment.checkGooglePlayAvailable() {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val status = apiAvailability.isGooglePlayServicesAvailable(requireContext())

    if (status != ConnectionResult.SUCCESS) {
        if (apiAvailability.isUserResolvableError(status)) {
            apiAvailability.getErrorDialog(this, status, 1)?.show()
        } else {
            Snackbar.make(
                this.requireActivity().window.decorView.rootView,
                getString(R.string.google_play_servicies_error),
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }
}

fun Fragment.hasPermission(permission: Array<String>): Boolean {
    return permission.none {
        ActivityCompat.checkSelfPermission(
            requireContext(),
            it
        ) != PackageManager.PERMISSION_GRANTED
    }
}

fun Fragment.isInternetAvailable(): Boolean {
    var result = false
    val connectivityManager =
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}

fun Activity.showSnackBar(
    mainTextStringId: Int,
    actionStringId: Int,
    listener: View.OnClickListener
) {
    Snackbar.make(
        findViewById(android.R.id.content),
        getString(mainTextStringId),
        Snackbar.LENGTH_INDEFINITE
    ).setAction(getString(actionStringId), listener).show()
}

fun Fragment.showToast(message: String, length: Int) {
    Toast.makeText(
        context,
        message,
        length
    ).show()
}
