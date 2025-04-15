package com.example

import io.kvision.*
import io.kvision.core.AlignItems
import io.kvision.core.Border
import io.kvision.core.BorderStyle
import io.kvision.core.Col
import io.kvision.core.Color
import io.kvision.core.Overflow
import io.kvision.core.onEvent
import io.kvision.form.select.tomSelectInput
import io.kvision.html.ButtonStyle
import io.kvision.i18n.DefaultI18nManager
import io.kvision.i18n.I18n
import io.kvision.i18n.I18n.tr
import io.kvision.modal.Modal
import io.kvision.pace.Pace
import io.kvision.panel.hPanel
import io.kvision.panel.root
import io.kvision.panel.tab
import io.kvision.panel.tabPanel
import io.kvision.panel.vPanel
import io.kvision.routing.Routing
import io.kvision.theme.ThemeManager
import io.kvision.theme.themeSwitcher
import io.kvision.utils.auto
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.useModule

@JsModule("/kotlin/modules/i18n/messages-pl.json")
external val messagesPl: dynamic

@JsModule("/kotlin/modules/i18n/messages-en.json")
external val messagesEn: dynamic

@JsModule("/kotlin/modules/css/showcase.css")
external val cssShowcase: dynamic

@JsModule("react-awesome-button/dist/themes/theme-blue.css")
external val cssThemeBlue: dynamic

@JsModule("pace-progressbar/themes/blue/pace-theme-flash.css")
external val paceThemeFlash: dynamic

class Showcase : Application() {
    init {
        Routing.init()
        Pace.init(paceThemeFlash)
        ThemeManager.init()
        useModule(cssShowcase)
        useModule(cssThemeBlue)
        if (!(I18n.language in listOf("en", "pl"))) {
            I18n.language = "en"
        }
    }

    override fun start() {
        I18n.manager =
            DefaultI18nManager(
                mapOf(
                    "pl" to messagesPl,
                    "en" to messagesEn
                )
            )
        root("showcase") {
            vPanel {
                width = 100.perc
                tabPanel(scrollableTabs = true) {
                    width = 80.perc
                    margin = 20.px
                    marginLeft = auto
                    marginRight = auto
                    padding = 20.px
                    overflow = Overflow.HIDDEN
                    border = Border(2.px, BorderStyle.SOLID, Color.name(Col.SILVER))
                    tab(tr("HTML"), "fas fa-bars", route = "/basic") {
                        add(BasicTab())
                    }
                    tab(tr("Forms"), "fas fa-edit", route = "/forms") {
                        add(FormTab())
                    }
                    tab(tr("Buttons"), "far fa-check-square", route = "/buttons") {
                        add(ButtonsTab())
                    }
                    tab(tr("Dropdowns"), "fas fa-arrow-down", route = "/dropdowns") {
                        add(DropDownTab())
                    }
                    tab(tr("Containers"), "fas fa-database", route = "/containers") {
                        add(ContainersTab())
                    }
                    tab(tr("Layouts"), "fas fa-th-list", route = "/layouts") {
                        add(LayoutsTab())
                    }
                    tab(tr("Windows"), "fas fa-window-maximize", route = "/windows") {
                        add(ModalsTab())
                    }
                    tab(tr("Data binding"), "fas fa-retweet", route = "/data") {
                        add(DataTab())
                    }
                    tab(tr("Drag & Drop"), "fas fa-arrows-alt", route = "/dragdrop") {
                        add(DragDropTab())
                    }
                    tab(tr("Charts"), "far fa-chart-bar", route = "/charts") {
                        add(ChartTab())
                    }
                    tab(tr("Tables"), "fas fa-table", route = "/tabulator") {
                        add(TabulatorTab())
                    }
                    tab(tr("RESTful"), "fas fa-plug", route = "/restful") {
                        add(RestTab())
                    }
                    onEvent {
                        changeTab = {
                            Modal.closeAllModals()
                        }
                    }
                }
                hPanel(spacing = 20, useWrappers = true, alignItems = AlignItems.CENTER) {
                    marginLeft = auto
                    marginRight = auto
                    marginBottom = 100.px
                    tomSelectInput(listOf("en" to tr("English"), "pl" to tr("Polish")), I18n.language) {
                        width = 320.px
                        onEvent {
                            change = {
                                I18n.language = self.value ?: "en"
                            }
                        }
                    }
                    themeSwitcher(style = ButtonStyle.OUTLINESECONDARY, round = true)
                }
            }
        }
    }
}

fun main() {
    startApplication(
        ::Showcase,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        FontAwesomeModule,
        DatetimeModule,
        TomSelectModule,
        BootstrapUploadModule,
        RichTextModule,
        ChartModule,
        TabulatorModule,
        TabulatorCssBootstrapModule,
        ImaskModule,
        ToastifyModule,
        CoreModule
    )
}
