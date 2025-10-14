package com.example.BankProject.service.user;

import com.example.BankProject.exception.BlockedException;
import com.example.BankProject.exception.DuplicateDataException;
import com.example.BankProject.exception.EntityNotFoundException;
import com.example.BankProject.model.request.UserEditRequest;

import java.util.UUID;

/**
 * Сервис для управления пользователями системы.
 * Определяет основной функционал, связанный с редактированием, блокировкой и удалением пользователей.
 * Реализация интерфейса выполняется в классе {@code UserServiceImpl}.
 */
public interface UserService {

    /**
     * Изменяет данные пользователя.
     * Метод выполняет частичное обновление данных пользователя (ФИО, email, логин).
     * Если соответствующее поле не указано или пустое — оно не изменяется.
     *
     * @param userId  уникальный идентификатор пользователя, данные которого необходимо изменить
     * @param request объект с новыми данными пользователя ({@link UserEditRequest})
     * @throws EntityNotFoundException если пользователь с таким userId не найден
     * @throws DuplicateDataException  если указанный email или логин уже заняты другими пользователями
     */
    void editUser(UUID userId, UserEditRequest request);

    /**
     * Удаляет пользователя из системы.
     * После удаления пользователь больше не будет доступен в базе данных.
     *
     * @param userId уникальный идентификатор пользователя, которого требуется удалить
     * @throws EntityNotFoundException если пользователь с таким {@code userId} не найден
     */
    void deleteUser(UUID userId);

    /**
     * Блокирует пользователя.
     * Если пользователь уже имеет статус BLOCKED, будет выброшено исключение.
     *
     * @param userId уникальный идентификатор пользователя, которого нужно заблокировать
     * @throws EntityNotFoundException если пользователь с таким userId не найден
     * @throws BlockedException если пользователь уже заблокирован
     */
    void blockUser(UUID userId);
}