package mn.turbo.weather.common

data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String = ""
)
