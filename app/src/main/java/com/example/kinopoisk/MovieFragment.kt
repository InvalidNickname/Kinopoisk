package com.example.kinopoisk

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MovieFragment(private val movieId: Int) : Fragment() {

    private val client = OkHttpClient()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<ImageView>(R.id.back_button)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, MainFragment(), "home").commit()
        }
        view?.findViewById<Button>(R.id.errorRepeat)?.setOnClickListener {
            requestMovieInfo()
        }
        requestMovieInfo()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun requestMovieInfo() {
        view?.findViewById<ImageView>(R.id.errorImage)?.visibility = View.GONE
        view?.findViewById<TextView>(R.id.errorText)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.errorRepeat)?.visibility = View.GONE
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE

        val request = Request.Builder()
            .url("https://kinopoiskapiunofficial.tech/api/v2.2/films/$movieId")
            .header("X-API-KEY", "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                if (body != null) {
                    val bodyJSON = JSONObject(body)
                    val posterURL = bodyJSON.getString("posterUrl")
                    val name = bodyJSON.getString("nameRu")
                    val description = bodyJSON.getString("description")

                    val genres = bodyJSON.getJSONArray("genres")
                    var genre = ""
                    for (j in 0 until genres.length()) {
                        genre += genres.getJSONObject(j).getString("genre") + ", "
                    }
                    genre = genre.removeSuffix(", ")

                    val countries = bodyJSON.getJSONArray("genres")
                    var country = ""
                    for (j in 0 until countries.length()) {
                        country += countries.getJSONObject(j).getString("genre") + ", "
                    }
                    country = country.removeSuffix(", ")

                    var fullDescription = ""
                    if (context != null) {
                        fullDescription = String.format(context!!.getString(R.string.movie_desc_all, description, genre, country))
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        if (context != null && view != null) {
                            Glide.with(context!!).load(posterURL).listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                    return false
                                }

                                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                    completeLoad()
                                    return false
                                }
                            }).into(view!!.findViewById(R.id.poster))
                            view!!.findViewById<TextView>(R.id.name).text = name
                            view!!.findViewById<TextView>(R.id.movie_desc).text = fullDescription
                        }
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

    private fun completeLoad() {
        view?.findViewById<TextView>(R.id.name)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.movie_desc)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.poster)?.alpha = 1.0F
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
    }

    private fun setApiErrorInterface() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.errorImage)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.errorText)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.errorRepeat)?.visibility = View.VISIBLE
    }
}