// --- 👇👇👇 ¡LA CORRECCIÓN QUE ELIMINA EL ERROR DE SINTAXIS! 👇👇👇 ---// PRIMERO va el package, SIEMPRE.
package com.example.aplicacionjetpack.data.repository

// SEGUNDO van TODOS los imports.
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.api.NotificacionApi
import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import javax.inject.Inject
import kotlin.Result
// --- ------------------------------------------------------------------ ---


// La clase DEBE declarar que implementa la interfaz que Hilt está intentando "bindear".
class NotificacionRepositoryImpl @Inject constructor(
    private val api: NotificacionApi
) : NotificacionRepository { // <-- ESTA PARTE ": NotificacionRepository" FALTABA

    override suspend fun getNotificaciones(): Result<List<NotificacionResponse>> {
        return try {
            val userId = AuthManager.userId ?: return Result.failure(Exception("Usuario no autenticado"))
            Result.success(api.getNotificacionesByUsuario(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarLeida(id: Long): Result<Unit> {
        return try {
            api.marcarComoLeida(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
