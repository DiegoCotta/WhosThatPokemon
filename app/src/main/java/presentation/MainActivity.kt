package presentation

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import android.widget.Toast
import api.PokemonApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import model.Pokemon
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import model.Genaration
import shared.Image

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var job: Job = Job()
    private var api: PokemonApi = PokemonApi()
    private var pokemon: Pokemon? = null
    private var wasAnswered = false
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = SpinnerAdapter(this, Genaration.getArray())
        spGeneration.adapter = adapter

        searchPokemon()

        button.setOnClickListener {
            if (!wasAnswered) {
                if (edit_text.text.toString().toLowerCase() == pokemon?.name?.toLowerCase()) {
                    edit_text.setTextColor(ContextCompat.getColor(this, R.color.correct))
                    val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.correct))
                    ViewCompat.setBackgroundTintList(edit_text, colorStateList);
                } else {
                    edit_text.setTextColor(ContextCompat.getColor(this, R.color.wrong))
                    val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.wrong))
                    ViewCompat.setBackgroundTintList(edit_text, colorStateList);
                    edit_text.setText(pokemon?.name)
                }
                edit_text.isEnabled = false
                pokemon_sprite.colorFilter = null
                button.text = "next"
            } else {
                edit_text.isEnabled = true
                edit_text.setText("")
                searchPokemon()
                button.text = "check"
                edit_text.setTextColor(ContextCompat.getColor(this, R.color.pokemon_yellow))
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pokemon_yellow))
                ViewCompat.setBackgroundTintList(edit_text, colorStateList);
            }
            wasAnswered = !wasAnswered
        }

    }

    private fun searchPokemon() {
        flLoading.visibility = View.VISIBLE
        api.getPokemon(
            Random.nextInt(1, (spGeneration.selectedItem as Genaration).maxNumber),
            success = ::onPokemonSuccess,
            failure = ::handleError
        )
    }

    private fun onPokemonSuccess(p: Pokemon) {
        pokemon = p
        api.getPokemonSprite(
            p.sprites,
            success = {
                launch(Dispatchers.Main) {
                    flLoading.visibility = View.GONE
                    pokemon_sprite.setImageBitmap(it)
                    pokemon_sprite.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.pokemon_shadow
                        ), PorterDuff.Mode.SRC_IN
                    )

                }
            },
            failure = ::handleError
        )


    }

    private fun handleError(throwable: Throwable?) {

        throwable?.printStackTrace()
        launch(Dispatchers.Main) {
            flLoading.visibility = View.GONE
            val msg = throwable?.message ?: "Unknown error"
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}