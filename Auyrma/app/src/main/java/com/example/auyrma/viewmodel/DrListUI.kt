import androidx.annotation.StringRes
import com.example.auyrma.model.entity.Dr

sealed interface DrListUI {
    data class Loading(val isLoading: Boolean) : DrListUI
    data class Error(@StringRes val errorMessage: Int) : DrListUI
    data class Success(val doctorList: List<Dr>) : DrListUI
    data object Empty : DrListUI
    data class DrInserted(val doctor: Dr) : DrListUI
}
