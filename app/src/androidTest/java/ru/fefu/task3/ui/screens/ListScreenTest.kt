package ru.fefu.task3.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.ui.ListUiState
import ru.fefu.task3.ui.theme.Task3Theme

/**
 * инструментальный тест для экрана списка.
 * проверяет корректность отображения данных и взаимодействие с UI.
 */
class ListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun successState_showsListOfAnime() {
        val mockAnimes = listOf(
            AnimeBase(1, "Naruto", "Наруто", null, "8.3", "tv", "released"),
            AnimeBase(2, "One Piece", "Ван Пис", null, "8.6", "tv", "ongoing")
        )

        composeTestRule.setContent {
            Task3Theme {
                ListScreen(
                    uiState = ListUiState.Success(mockAnimes),
                    searchQuery = "",
                    onEvent = {},
                    onAnimeClick = {},
                    onFavouritesClick = {},
                    onRecentClick = {},
                    onSettingsClick = {}
                )
            }
        }

        // проверяем, что названия аниме отображаются на экране
        composeTestRule.onNodeWithText("Наруто").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ван Пис").assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetryButton() {
        composeTestRule.setContent {
            Task3Theme {
                ListScreen(
                    uiState = ListUiState.Error("Test error"),
                    searchQuery = "",
                    onEvent = {},
                    onAnimeClick = {},
                    onFavouritesClick = {},
                    onRecentClick = {},
                    onSettingsClick = {}
                )
            }
        }

        // проверяем наличие кнопки повтора при ошибке
        composeTestRule.onNodeWithText("Повторить").assertIsDisplayed()
    }

    @Test
    fun clickingAnime_triggersCallback_nonTrivial() {
        var clickedId: Long? = null
        val mockAnimes = listOf(
            AnimeBase(123, "Naruto", "Наруто", null, "8.3", "tv", "released")
        )

        composeTestRule.setContent {
            Task3Theme {
                ListScreen(
                    uiState = ListUiState.Success(mockAnimes),
                    searchQuery = "",
                    onEvent = {},
                    onAnimeClick = { clickedId = it },
                    onFavouritesClick = {},
                    onRecentClick = {},
                    onSettingsClick = {}
                )
            }
        }

        // кликаем по карточке
        composeTestRule.onNodeWithText("Наруто").performClick()

        // проверяем нетривиальный контракт: передается именно правильный ID
        assert(clickedId == 123L)
    }
}
