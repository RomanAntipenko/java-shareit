package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.exceptions.InCorrectBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Override
    public Item createItem(long userId, ItemDto itemDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.debug("userId not found in create method");
            throw new UserIdNotFoundException(String.format("itemId: \"%s\" не найден", userId));
        }
        Item item = ItemMapper.mapToItem(userOptional.get(), itemDto);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (userOptional.isEmpty()) {
            log.debug("userId not found in update method");
            throw new UserIdNotFoundException(String.format("itemId: \"%s\" не найден", userId));
        }
        if (itemOptional.isEmpty()) {
            log.debug("itemId not found in update method");
            throw new ItemIdNotFoundException(String.format("itemId: \"%s\" не найден", itemId));
        }
        Item item = ItemMapper.mapToItem(itemId, userOptional.get(), itemDto);
        if (item.getAvailable() != null) {
            itemOptional.get().setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            itemOptional.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemOptional.get().setDescription(item.getDescription());
        }
        return itemRepository.save(itemOptional.get());
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getAllItemsByOwner method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }

        if (itemRepository.findAllByOwnerId(userId) == null) {
            throw new ItemIdNotFoundException(String.format("item у пользователя с \"%s\" не найден", userId));
        }
        List<ItemDto> itemsDto = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
        itemsDto.forEach(this::addLastAndNextBookingToItem);
        return itemsDto;
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getItemById method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.debug("itemId not found in getItemById method");
            throw new ItemIdNotFoundException(String.format("itemId: \"%s\" не найден", itemId));
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        ItemDto itemDto = ItemMapper.mapToDto(itemOptional.get());
        if (itemOptional.get().getOwner().getId().equals(userId)) {
            addLastAndNextBookingToItem(itemDto);
        }
        itemDto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList()));
        //TODO доделать комментарии
        return itemDto;
    }

    private void addLastAndNextBookingToItem(ItemDto itemDto) {
        LocalDateTime rightNow = LocalDateTime.now();
        Booking nextBooking = bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
                itemDto.getId(), rightNow, BookingState.REJECTED);
        Booking lastBooking = bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
                itemDto.getId(), rightNow, BookingState.REJECTED);
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.mapToShortBooking(nextBooking));
        } else itemDto.setNextBooking(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.mapToShortBooking(lastBooking));
        } else itemDto.setLastBooking(null);
    }

    @Override
    public Collection<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.debug("User id not found in postComment method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.debug("itemId not found in postComment method");
            throw new ItemIdNotFoundException(String.format("itemId: \"%s\" не найден", itemId));
        }
        LocalDateTime rightMoment = LocalDateTime.now();
        Comment comment = CommentMapper
                .mapToComment(userOptional.get(), itemOptional.get(), commentDto, rightMoment);
        Booking lastBooking = bookingRepository.findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
                itemId, rightMoment, BookingState.REJECTED);
        if (lastBooking == null) {
            log.debug("This item wasn't booking by anyone earlier. In postComment method");
            throw new InCorrectBookingException("Этот предмет не был забронирован никем ранее, " +
                    "поэтому невозможно написать комментарий");
        }
        if (lastBooking.getBooker().getId() != userId) {
            log.debug("This user wasn't booking this item earlier. In postComment method");
            throw new InCorrectBookingException("Этот предмет не был забронирован этим пользователем ранее, " +
                    "поэтому невозможно написать комментарий");
        }
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(savedComment);
    }
}
