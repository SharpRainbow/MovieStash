package ru.mirea.moviestash

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.mirea.moviestash.search.SearchMovieFragment
import ru.mirea.moviestash.search.SearchPersonFragment

class ViewPagerAdapter(activity: AppCompatActivity, private val names: List<String>) :
    FragmentStateAdapter(activity) {

    val fragments = mutableListOf<Fragment>()

    init {
        fragments.add(SearchMovieFragment.newInstance())
        fragments.add(SearchPersonFragment.newInstance())
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getName(position: Int): String {
        return names[position]
    }
}