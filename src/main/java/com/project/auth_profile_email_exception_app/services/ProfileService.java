package com.project.auth_profile_email_exception_app.services;

import com.project.auth_profile_email_exception_app.auth.repositories.UserRepository;
import com.project.auth_profile_email_exception_app.dto.ProfileModificationDto;
import com.project.auth_profile_email_exception_app.dto.ProfileDto;
import com.project.auth_profile_email_exception_app.exceptions.InvalidOperationException;
import com.project.auth_profile_email_exception_app.exceptions.ResourceNotFoundException;
import com.project.auth_profile_email_exception_app.responses.ProfilePageResponse;
import com.project.auth_profile_email_exception_app.entities.Follower;
import com.project.auth_profile_email_exception_app.entities.Profile;
import com.project.auth_profile_email_exception_app.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, FollowerRepository followerRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;

    }


    public List<Profile> getAllUsers() {
        return profileRepository.findAll();
    }

    public void follow(Long userId, Long followingId){
        Optional<Follower> optionalFollower = followerRepository.findByUserIdAndFollowedId(userId,followingId);
        if (userId.equals(followingId)){
            throw new InvalidOperationException("User can't follow themself");
        } else if (optionalFollower.isPresent()){
            throw new InvalidOperationException("Already Followed");
        } else {
            Profile followingProfile = profileRepository.findById(followingId).orElseThrow(()-> new ResourceNotFoundException("no such profile found"));
            Profile profile = profileRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(" profile not found to follow"));
            Follower follower = new Follower();
            follower.setUserId(userId);
            follower.setFollowedId(followingId);
            follower.setCreatedAt(LocalDateTime.now());
            profile.setFollowing(profile.getFollowing() + 1);
            followingProfile.setFollowers(followingProfile.getFollowers() + 1);
            profileRepository.save(profile);
            profileRepository.save(followingProfile);

            followerRepository.save(follower);

        }
    }

    public void unFollow(Long userId, Long unfollowingId){
        Profile unfollowingProfile = profileRepository.findById(unfollowingId).orElseThrow(()-> new ResourceNotFoundException("no such profile found"));
        Profile profile = profileRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(" profile not found"));
        profile.setFollowing(profile.getFollowing() - 1);
        unfollowingProfile.setFollowers(unfollowingProfile.getFollowers() - 1);
        profileRepository.save(profile);
        profileRepository.save(unfollowingProfile);

        followerRepository.deleteByUserIdAndFollowedId(userId,unfollowingId);
    }

    public List<Long> getFollowers(Long profileId) {
        return followerRepository.findFollowerProfileIds(profileId);
    }

    public List<Long> getFollowings(Long profileId) {
        return followerRepository.findFollowingProfileIds(profileId);

    }


    public ProfilePageResponse getProfileDtoResponsesByIds(List<Long> profileIds, Integer pageNumber, Integer pageSize){

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Profile> pageProfileList = profileRepository.findByIdIn(profileIds, pageable);
        List<Profile> profileList = pageProfileList.getContent();

        // Profile To ProfileNameDto
        List<ProfileDto> profileDtoList = new ArrayList<>();
        for(Profile profile : profileList){
            ProfileDto profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUserName(),
                    profile.getName(),
                    profile.getProfilePic()
            );
            profileDtoList.add(profileDto);
        }

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileList.getTotalElements(), pageProfileList.getTotalPages(), pageProfileList.isLast());
    }


    public Profile getUserById(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
    }


    public ProfilePageResponse searchProfilesByName(String name, Integer pageNumber, Integer pageSize) {
//        return profileRepository.findByNameContainingIgnoreCase(name);

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Profile> pageProfileList = profileRepository.findByUsernameContainingIgnoreCase(name, pageable);
        List<Profile> profileList = pageProfileList.getContent();

        // Profile To ProfileNameDto
        List<ProfileDto> profileDtoList = new ArrayList<>();
        for(Profile profile : profileList){
            ProfileDto profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUserName(),
                    profile.getName(),
                    profile.getProfilePic()
            );
            profileDtoList.add(profileDto);
        }

        return new ProfilePageResponse(profileDtoList, pageNumber, pageSize,
                pageProfileList.getTotalElements(), pageProfileList.getTotalPages(), pageProfileList.isLast());

    }


    public Profile updateProfile(ProfileModificationDto profileModificationDto, Long ownerId){
        Profile profile = profileRepository.findById(ownerId).orElseThrow(()-> new ResourceNotFoundException(" profile not found"));
        profile.setName(profileModificationDto.getName());
        profile.setAge(profileModificationDto.getAge());
        profile.setBio(profileModificationDto.getBio());
        profile.setProfilePic(profileModificationDto.getProfilePic());
        return profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(()-> new ResourceNotFoundException(" profile not found"));
        // Adjust follow counts
        profileRepository.decrementFollowingCountByProfileId(profileId);
        profileRepository.decrementFollowersCountByProfileId(profileId);

        // Adjust likes and cmt Counts

        // Delete related entities (posts, likes, comments, habits, followTable)
        followerRepository.deleteByUserIdOrFollowedId(profileId);


        // Finally, delete the profile and user
        profileRepository.deleteById(profileId);  // delete profile bfr user cuz foreignConstrnt
        userRepository.deleteById(profileId);
    }


}