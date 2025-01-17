package ca.gbc.friendservice.service;

import ca.gbc.friendservice.config.UserServiceClient;
import ca.gbc.friendservice.model.Friendship;
import ca.gbc.friendservice.model.FriendshipStatus;
import ca.gbc.friendservice.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public void createFriendship(String userId, String friendId) {
        userServiceClient.getUserDetails(userId)
                .flatMap(user -> userServiceClient.getUserDetails(friendId))
                .flatMap(user -> Mono.fromCallable(() -> {
                    Friendship friendship = new Friendship();
                    friendship.setUserId(userId);
                    friendship.setFriendId(friendId);
                    friendship.setStatus(FriendshipStatus.PENDING);
                    Friendship created = friendshipRepository.save(friendship);
                    log.info("Friend request from {} to {}.", userId, friendId);
                    return created;
                }))
                .then();
    }

    @Override
    public void acceptFriendship(String userId, String friendId) {
        Mono.fromCallable(() ->
                        friendshipRepository.findByUserIdAndFriendId(friendId, userId)
                                .orElseThrow(() -> new RuntimeException("Friend request not found"))
                )
                .map(friendship -> {
                    friendship.setStatus(FriendshipStatus.ACCEPTED);
                    friendshipRepository.save(friendship);
                    log.info("Friend request accepted between {} and {}.", userId, friendId);
                    return friendship;
                })
                .then();
    }

    @Override
    public void rejectFriendship(String userId, String friendId) {
        Mono.fromCallable(() ->
                        friendshipRepository.findByUserIdAndFriendId(friendId, userId)
                                .orElseThrow(() -> new RuntimeException("Friend request not found"))
                )
                .map(friendship -> {
                    friendship.setStatus(FriendshipStatus.REJECTED);
                    friendshipRepository.save(friendship);
                    log.info("Friend request between {} and {} has been rejected.", userId, friendId);
                    return friendship;
                })
                .then();
    }

    @Override
    public void deleteFriendship(String userId, String friendId) {
        Mono.fromCallable(() ->
                        friendshipRepository.findByUserIdAndFriendId(userId, friendId)
                                .orElseThrow(() -> new RuntimeException("Friend not found"))
                )
                .doOnSuccess(friendship -> {
                    friendshipRepository.delete(friendship);
                    log.info("Friendship between {} and {} has been deleted.", userId, friendId);
                })
                .then();
    }

    @Override
    public Mono<String> getFriendshipStatus(String userId, String friendId) {
        return Mono.fromCallable(() ->
                friendshipRepository.findByUserIdAndFriendId(userId, friendId)
                        .map(Friendship::getStatus)
                        .map(Enum::name)
                        .orElse("Not a friend")
        );
    }
}
