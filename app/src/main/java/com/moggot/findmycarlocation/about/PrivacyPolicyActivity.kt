package com.moggot.findmycarlocation.about

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.common.BaseActivity
import com.moggot.findmycarlocation.databinding.ActivityPrivacyPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url: String = getString(R.string.privacy_policy_link)
        viewBinding.privacyPolicyWebview.webViewClient = WebViewClient()
        val webSettings: WebSettings = viewBinding.privacyPolicyWebview.settings
        webSettings.javaScriptEnabled = true
        viewBinding.privacyPolicyWebview.loadUrl(url)
    }

    override fun performDataBinding(): ActivityPrivacyPolicyBinding {
        return ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }
}
