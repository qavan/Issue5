package com.example.issue5

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlin.reflect.KClass

inline fun <reified T: Fragment> KClass<T>.createFragment() = constructors.first().call()
inline fun <reified T: Fragment> FragmentManager.addFragment(fragment: KClass<T>, containerId: Int) {
    commit {
        val fragmentInstance = fragment.createFragment()
        add(containerId, fragmentInstance, fragmentInstance::class.simpleName)
    }
}
fun fragmentContainerView(context: Context, id: Int) = FragmentContainerView(context).apply {
    this@apply.id = id
}

// level 1
class FragmentRoot: Fragment() {

    companion object {
        const val viewId = 60001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.addFragment(FragmentTab::class, viewId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = fragmentContainerView(requireContext(), viewId)
}

// level 2
class FragmentTab: Fragment() {
    companion object {

        const val viewId = 60002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.addFragment(FragmentPager::class, viewId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = fragmentContainerView(requireContext(), viewId)
}

// level 3
class FragmentPager: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = ViewPager2(requireContext()).apply {
        orientation = ViewPager2.ORIENTATION_HORIZONTAL
        adapter = PagerAdapter(this@FragmentPager)
        offscreenPageLimit = 2
    }
}

// level 4
class PagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 100

    override fun createFragment(position: Int): Fragment {
        return FragmentPagerItem()
    }
}

class FragmentPagerItem: Fragment() {

    class PagerItemViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
        val stateHoldersHolder = savedStateHandle.get<StateHoldersHolder>("holder") ?: kotlin.run {
            val holder = StateHoldersHolder()
            savedStateHandle["holder"] = holder
            holder
        }
    }

    private val viewModel by viewModels<PagerItemViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ComposableRoot(viewModel.stateHoldersHolder)
        }
    }
}