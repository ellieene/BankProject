package com.example.BankProject.service.auth;

import com.example.BankProject.exception.DuplicateDataException;
import com.example.BankProject.exception.EntityNotFoundException;
import com.example.BankProject.exception.InvalidCredentialsException;
import com.example.BankProject.model.request.UserAuthorizationRequest;
import com.example.BankProject.model.request.UserRegistrationRequest;

/**
 * Сервис для обработки операций аутентификации и регистрации пользователей.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>Регистрации новых пользователей в системе</li>
 *   <li>Аутентификации существующих пользователей</li>
 * </ul>
 *
 * @see UserRegistrationRequest
 * @see UserAuthorizationRequest
 */
public interface AuthService {

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param userRegistrationRequest объект запроса на регистрацию, содержащий данные пользователя
     * @throws DuplicateDataException если пользователь с таким email или логином уже существует
     */
    void registration(UserRegistrationRequest userRegistrationRequest);

    /**
     * Аутентифицирует пользователя и генерирует JWT токен.
     *
     * @param userAuthorizationRequest объект запроса на авторизацию, содержащий учетные данные
     * @return JWT токен для аутентифицированного пользователя
     * @throws InvalidCredentialsException если аутентификация не удалась из-за пароля
     * @throws EntityNotFoundException если пользователь с указанными данными не найден
     */
    String authorization(UserAuthorizationRequest userAuthorizationRequest);
}
