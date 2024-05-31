package ru.mirea.moviestash

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.mirea.moviestash.search.SearchMovieFragment
import ru.mirea.moviestash.search.SearchPersonFragment

class ViewPagerAdapter(activity: AppCompatActivity, private val names: List<String>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return names.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchMovieFragment.newInstance()
            else -> SearchPersonFragment.newInstance()
        }
    }

    fun getName(position: Int): String {
        return names[position]
    }
}