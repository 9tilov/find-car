package com.moggot.findmycarlocation.about

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.moggot.findmycarlocation.BuildConfig
import com.moggot.findmycarlocation.MainActivity
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.base.viewBinding
import com.moggot.findmycarlocation.common.BaseFragment
import com.moggot.findmycarlocation.databinding.FragmentAboutBinding
import java.util.Calendar

class AboutFragment : BaseFragment(R.layout.fragment_about) {

    override val viewModel by viewModels<AboutViewModel>()
    override val fragmentTag: String = "AboutFragment"
    private val viewBinding by viewBinding(FragmentAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.aboutTvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        viewBinding.aboutTvCopyright.text = getString(
            R.string.copyright,
            Calendar.getInstance()[Calendar.YEAR]
        )
        val mainActivity: MainActivity = activity as? MainActivity ?: return
        if ((activity as MainActivity).isPremiumPurchased) {
            viewBinding.aboutClPurchasePremium.visibility = View.GONE
        }
        viewBinding.aboutClPrivacyPolicy.setOnClickListener {
            startActivity(Intent(context, PrivacyPolicyActivity::class.java))
        }
         viewBinding.aboutClPurchasePremium.setOnClickListener { v -> mainActivity.billingManager.requestSubscription() }
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
