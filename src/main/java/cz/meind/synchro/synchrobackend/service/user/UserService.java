package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.CheckRepository;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.DeleteUserDto;
import cz.meind.synchro.synchrobackend.dto.request.EditUserDto;
import cz.meind.synchro.synchrobackend.dto.response.UserListResponse;
import cz.meind.synchro.synchrobackend.dto.response.UserResponseEntity;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final SynchroConfig synchroConfig;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CheckRepository checkRepository;
    private final ValidationUtil validationUtil;
    private final RoleRepository roleRepository;

    /**
     * Constructor for UserService.
     *
     * @param synchroConfig Configuration for synchronization settings.
     * @param userRepository Repository to handle user data.
     * @param eventRepository Repository to handle event data.
     * @param checkRepository Repository to handle check-in/check-out data.
     * @param validationUtil Utility class for validation.
     * @param roleRepository Repository to handle role data.
     */
    public UserService(SynchroConfig synchroConfig, UserRepository userRepository, EventRepository eventRepository, CheckRepository checkRepository, ValidationUtil validationUtil, RoleRepository roleRepository) {
        this.synchroConfig = synchroConfig;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.checkRepository = checkRepository;
        this.validationUtil = validationUtil;
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return An Optional containing the UserListResponse with all user details.
     */
    public Optional<UserListResponse> queryUserList() {
        return Optional.of(new UserListResponse(userRepository.findAll().stream().map(user -> new UserResponseEntity(user.getId().toString(), user.getUsername(), user.getRole().toString(), user.getEmail(), user.getPhone(), user.getEnabled().toString())).toList()));
    }

    /**
     * Deletes a user based on the provided DeleteUserDto. If the user is the admin, deletion is not allowed.
     *
     * @param deleteUserDto Data Transfer Object containing the user details to be deleted.
     * @return True if the user is successfully deleted; false otherwise.
     */
    public boolean deleteUser(DeleteUserDto deleteUserDto) {
        if (deleteUserDto.getUsername().equals(synchroConfig.getAdminUsername())) return false; // Prevent deletion of the admin user
        Optional<UserEntity> u = userRepository.findByUsername(deleteUserDto.getUsername());
        if (u.isEmpty() || !u.get().getId().toString().equals(deleteUserDto.getId())) return false; // Validate user existence and ID
        updateSetEnabled(u.get()); // Disable or delete the user
        return true;
    }

    /**
     * Edits user information such as username, email, phone, and role. It also validates the provided information.
     *
     * @param editUserDto Data Transfer Object containing the user details to be edited.
     * @return True if the user is successfully edited; false otherwise.
     */
    public boolean editUser(EditUserDto editUserDto) {
        Optional<UserEntity> u = userRepository.findById(editUserDto.getId());
        if (u.isEmpty()) return false;
        if (u.get().getUsername().equals(synchroConfig.getAdminUsername())) return false; // Prevent editing the admin user
        if (!validationUtil.validateEmail(editUserDto.getEmail()) || !validationUtil.validatePhone(editUserDto.getPhone()))
            return false; // Validate email and phone
        if (!validationUtil.editCheck(editUserDto)) return false; // Check for validity of the edit
        updateUser(editUserDto); // Update user data in the database
        return true;
    }

    /**
     * Updates the 'enabled' status of a user. If the user is enabled, it will be disabled; if it's already disabled, the user will be deleted.
     *
     * @param u The user whose status is to be updated.
     */
    @Async
    protected void updateSetEnabled(UserEntity u) {
        if (u.getEnabled())
            userRepository.updateUserEnabledByUsername(u.getUsername(), false); // Disable the user
        else
            updateDelete(u); // Delete the user if already disabled
    }

    /**
     * Updates user details including username, password (if provided), role, email, and phone.
     *
     * @param u The EditUserDto containing the new user details.
     */
    @Async
    protected void updateUser(EditUserDto u) {
        if (!u.getPassword().isEmpty())
            userRepository.updateUserEntityById(u.getId(), u.getUsername(), validationUtil.hashPassword(u.getPassword()), roleRepository.findRoleEntityByName(u.getRole()).get(), u.getEmail(), u.getPhone());
        else
            userRepository.updateUserEntityById(u.getId(), u.getUsername(), roleRepository.findRoleEntityByName(u.getRole()).get(), u.getEmail(), u.getPhone());
    }

    /**
     * Deletes a user, including their related events and check-in/check-out records.
     *
     * @param u The user to be deleted.
     */
    @Async
    protected void updateDelete(UserEntity u) {
        eventRepository.deleteAll(u.getEvents()); // Delete related events
        checkRepository.deleteAll(u.getChecks()); // Delete related check-ins/check-outs
        userRepository.delete(u); // Delete the user
    }
}
