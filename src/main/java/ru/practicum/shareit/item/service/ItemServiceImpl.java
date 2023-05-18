package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(Item item) {
        if (validateUserId(item.getUserId())) {
            return itemRepository.createItem(item);
        }
        log.debug("User id not found in createItem method");
        throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", item.getUserId()));
    }

    @Override
    public Item patchItem(Item item) {
        if (validateUserId(item.getUserId())) {
            return itemRepository.patchItem(item);
        }
        log.debug("User id not found in patchItem method");
        throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", item.getUserId()));
    }

    @Override
    public Collection<Item> getAllItemsByOwner(long userId) {
        if (validateUserId(userId)) {
            return itemRepository.getItemsByUserId(userId);
        }
        log.debug("User id not found in getAllItemsByOwner method");
        throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
    }

    @Override
    public Collection<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.getItemsByText(text);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.getItemByItemId(itemId);
    }

    public boolean validateUserId(long userId) {
        return userRepository.getUsers().stream()
                .map(User::getId)
                .anyMatch((id -> id.equals(userId)));
    }
}
