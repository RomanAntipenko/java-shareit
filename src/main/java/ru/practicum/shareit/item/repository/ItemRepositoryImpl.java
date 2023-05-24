package ru.practicum.shareit.item.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.exceptions.OwnerIdMismatchException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    @Getter
    private final Map<Long, Item> itemMap;

    @Getter
    private final AtomicLong atomicId = new AtomicLong(0);

    @Override
    public Item createItem(Item item) {
        atomicId.getAndIncrement();
        item.setId(atomicId.longValue());
        itemMap.put(atomicId.longValue(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (!itemMap.containsKey(item.getId())) {
            log.debug("itemId not found in patch method");
            throw new ItemIdNotFoundException(String.format("itemId: \"%s\" не найден", item.getId()));
        }
        if (!itemMap.get(item.getId()).getUserId().equals(item.getUserId())) {
            log.debug("userId not found in patch method");
            throw new OwnerIdMismatchException("передан некорректный userId для сущности item");
        }
        Item itemBeforePatch = itemMap.get(item.getId());
        if (item.getAvailable() != null) {
            itemBeforePatch.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            itemBeforePatch.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemBeforePatch.setDescription(item.getDescription());
        }
        return itemBeforePatch;
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        List<Item> items = new ArrayList<>(itemMap.values());
        return items.stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsByText(String text) {
        List<Item> items = new ArrayList<>(itemMap.values());
        return items.stream()
                .filter(item -> (item.getAvailable())
                        && (item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemByItemId(long itemId) {
        if (itemMap.containsKey(itemId)) {
            return itemMap.get(itemId);
        }
        log.debug("itemId not found in getItemByItemId method");
        throw new ItemIdNotFoundException(String.format("itemId: \"%s\" не найден", itemId));
    }
}
