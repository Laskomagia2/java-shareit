package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.comment.dal.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;

    @Transactional
    public ItemDto postItem(Long ownerId, ItemCreateRequest request) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец с id " + ownerId + " не найден"));
        Item item = ItemMapper.mapToItem(request);
        item.setOwner(owner);
        return ItemMapper.mapToItemDto(itemStorage.save(item));
    }

    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        validateUser(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return itemStorage.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(item -> constructDto(item, ownerId, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        return constructDto(item, userId, LocalDateTime.now());
    }

    public Collection<ItemDto> getItemsByDescription(String desc) {
        if (desc == null || desc.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(desc).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateRequest newItem) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет прав на редактирование этой вещи");
        }

        if (newItem.getName() != null && !newItem.getName().isBlank()) item.setName(newItem.getName());
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) item.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null) item.setAvailable(newItem.getAvailable());

        return ItemMapper.mapToItemDto(itemStorage.save(item));
    }

    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (!bookingStorage.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, now.plusSeconds(1))) {
            throw new ValidationBadRequestException("Аренда еще не завершена");
        }

        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        User author = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Comment comment = Comment.builder()
                .text(request.getText())
                .item(item)
                .author(author)
                .created(now)
                .build();

        Comment savedComment = commentStorage.save(comment);
        commentStorage.flush();

        return mapToCommentDto(savedComment);
    }

    private ItemDto constructDto(Item item, Long userId, LocalDateTime now) {
        ItemDto dto = ItemMapper.mapToItemDto(item);

        dto.setComments(commentStorage.findAllByItemIdOrderByCreatedDesc(item.getId()).stream()
                .map(this::mapToCommentDto)
                .toList());

        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(bookingStorage
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), Status.APPROVED, now)
                    .map(b -> new ItemDto.BookingShortDto(b.getId(), b.getBooker().getId()))
                    .orElse(null));

            dto.setNextBooking(bookingStorage
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, now)
                    .map(b -> new ItemDto.BookingShortDto(b.getId(), b.getBooker().getId()))
                    .orElse(null));
        }

        return dto;
    }

    private CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    private void validateUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
