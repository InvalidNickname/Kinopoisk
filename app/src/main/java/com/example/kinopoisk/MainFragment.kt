package com.example.kinopoisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainFragment : Fragment() {

    private val client = OkHttpClient()
    private var movies = ArrayList<Movie>()
    private var recyclerView: RecyclerView? = null
    private val clicker = object : MovieAdapter.OnItemClickListener {
        override fun onItemClick(id: Int) {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, MovieFragment(id), "movie").commit()
        }
    }
    private var adapter = MovieAdapter(movies, clicker)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<Button>(R.id.errorRepeat)?.setOnClickListener {
            requestPopularMovies()
        }
        recyclerView = view?.findViewById(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        requestPopularMovies()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun requestPopularMovies() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.errorImage)?.visibility = View.GONE
        view?.findViewById<TextView>(R.id.errorText)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.errorRepeat)?.visibility = View.GONE

        val request = Request.Builder()
            .url("https://kinopoiskapiunofficial.tech/api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
            .header("X-API-KEY", "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                if (body != null) {
                    val moviesJSON = JSONObject(body).getJSONArray("films")
                    val newMovies = ArrayList<Movie>()
                    for (i in 0 until moviesJSON.length()) {
                        val movie = moviesJSON.getJSONObject(i)

                        val genres = movie.getJSONArray("genres")
                        var genre = ""
                        for (j in 0 until if (genres.length() > 2) 2 else genres.length()) {
                            genre += genres.getJSONObject(j).getString("genre") + ", "
                        }
                        genre = genre.removeSuffix(", ")

                        newMovies.add(
                            Movie(
                                movie.getInt("filmId"),
                                movie.getString("nameRu"),
                                movie.getString("year"),
                                genre,
                                URL(movie.getString("posterUrlPreview"))
                            )
                        )
                    }
                    movies.clear()
                    movies.addAll(newMovies)
                    GlobalScope.launch(Dispatchers.Main) {
                        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        setApiErrorInterface()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.Main) {
                    setApiErrorInterface()
                }
            }
        })
    }

    private fun setApiErrorInterface() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.errorImage)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.errorText)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.errorRepeat)?.visibility = View.VISIBLE
    }

}