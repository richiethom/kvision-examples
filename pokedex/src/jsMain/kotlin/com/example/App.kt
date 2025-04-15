package com.example

import io.kvision.Application
import io.kvision.BootstrapCssModule
import io.kvision.BootstrapModule
import io.kvision.CoreModule
import io.kvision.Hot
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.JustifyItems
import io.kvision.core.TextAlign
import io.kvision.core.onEvent
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.i18n.DefaultI18nManager
import io.kvision.i18n.I18n
import io.kvision.i18n.I18n.gettext
import io.kvision.i18n.I18n.tr
import io.kvision.panel.gridPanel
import io.kvision.panel.hPanel
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.redux.ActionCreator
import io.kvision.redux.createTypedReduxStore
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.startApplication
import io.kvision.state.bind
import io.kvision.toolbar.buttonGroup
import io.kvision.utils.auto
import io.kvision.utils.obj
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.browser.document
import kotlinx.serialization.builtins.ListSerializer

@JsModule("hammerjs")
external val hammerjs: dynamic

@JsModule("/kotlin/modules/i18n/messages-en.json")
external val messagesEn: dynamic

@JsModule("/kotlin/modules/i18n/messages-pl.json")
external val messagesPl: dynamic

class App : Application() {

    private val store = createTypedReduxStore(::pokedexReducer, Pokedex(false, null, listOf(), listOf(), null, 0, 1))

    override fun start() {
        I18n.manager =
            DefaultI18nManager(
                mapOf(
                    "en" to messagesEn,
                    "pl" to messagesPl
                )
            )

        root("kvapp") {
            vPanel(alignItems = AlignItems.STRETCH, useWrappers = true) {
                marginTop = 10.px
                width = 100.perc
                searchField()
                vPanel(alignItems = AlignItems.STRETCH, useWrappers = true).bind(store) { state ->
                    maxWidth = 1200.px
                    textAlign = TextAlign.CENTER
                    marginLeft = auto
                    marginRight = auto
                    informationText(state)
                    if (!state.downloading && state.errorMessage == null) {
                        pokemonGrid(state)
                        pagination(state)
                    }
                }
            }
        }
        store.dispatch(downloadPokemons())
        val hammerjs = hammerjs(document.body)
        hammerjs.on("swiperight") {
            store.dispatch(PokeAction.PrevPage)
        }
        hammerjs.on("swipeleft") {
            store.dispatch(PokeAction.NextPage)
        }
    }

    private fun Container.searchField() {
        text {
            placeholder = tr("Enter pokemon name ...")
            width = 300.px
            marginLeft = auto
            marginRight = auto
            autofocus = true
            onEvent {
                input = {
                    store.dispatch(PokeAction.SetSearchString(self.value))
                }
            }
        }
    }

    private fun Container.informationText(state: Pokedex) {
        if (state.downloading) {
            div(tr("Loading ..."))
        } else if (state.errorMessage != null) {
            div(state.errorMessage)
        }
    }

    private fun Container.pokemonGrid(state: Pokedex) {
        gridPanel(
            templateColumns = "repeat(auto-fill, minmax(250px, 1fr))",
            justifyItems = JustifyItems.CENTER
        ) {
            state.visiblePokemons.forEach {
                add(PokeBox(it))
            }
        }
    }

    private fun Container.pagination(state: Pokedex) {
        hPanel(justify = JustifyContent.CENTER) {
            margin = 30.px
            buttonGroup {
                button("<<") {
                    disabled = state.pageNumber == 0
                    onClick {
                        store.dispatch(PokeAction.PrevPage)
                    }
                }
                button(" ${state.pageNumber + 1} / ${state.numberOfPages} ", disabled = true)
                button(">>") {
                    disabled = state.pageNumber == (state.numberOfPages - 1)
                    onClick {
                        store.dispatch(PokeAction.NextPage)
                    }
                }
            }
        }
    }

    private fun downloadPokemons(): ActionCreator<dynamic, Pokedex> {
        return { dispatch, _ ->
            val restClient = RestClient()
            dispatch(PokeAction.StartDownload)
            restClient.call<List<Pokemon>>("https://pokeapi.co/api/v2/pokemon/") {
                data = obj { limit = 800 }
                resultTransform = { it.results }
                deserializer = ListSerializer(Pokemon.serializer())
            }.then { list ->
                dispatch(PokeAction.DownloadOk)
                dispatch(PokeAction.SetPokemonList(list))
                dispatch(PokeAction.SetSearchString(null))
            }.catch { e ->
                val info = if (!e.message.isNullOrBlank()) {
                    " (${e.message})"
                } else {
                    ""
                }
                dispatch(PokeAction.DownloadError(gettext("Service error!") + info))
            }
        }
    }
}

fun main() {
    startApplication(
        ::App,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        CoreModule
    )
}
