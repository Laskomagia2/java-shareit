package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dal.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public ItemRequestDto postItemRequest(Long ownerId, ItemRequestCreateRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец с id " + ownerId + " не найден"));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request);
        itemRequest.setRequestor(owner);
        return ItemRequestMapper.mapToItemRequestDto(requestRepository.save(itemRequest));
    }

    public Collection<ItemRequestWithItemsDto> getItemRequestForUser(Long userId) {
        validateUser(userId);
        Collection<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestsId = requests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestWithItemsDto dto = ItemRequestMapper.toItemRequestWithItemsDto(request);
                    dto.setItems(itemsByRequest.getOrDefault(request.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    public Collection<ItemRequestWithItemsDto> getAllOtherRequests(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Collection <ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, pageable);

        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestWithItemsDto dto = ItemRequestMapper.toItemRequestWithItemsDto(request);
                    dto.setItems(itemsByRequest.getOrDefault(request.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    public ItemRequestWithItemsDto getRequestById(Long requestId) {
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с id " + requestId + " не найден")
        );
        ItemRequestWithItemsDto requestDto = ItemRequestMapper.toItemRequestWithItemsDto(request);
        requestDto.setItems(
                itemRepository.findAllByRequestId(requestId).stream().map(ItemMapper::mapToItemDto).toList()
        );
        return requestDto;
    }



    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

}
