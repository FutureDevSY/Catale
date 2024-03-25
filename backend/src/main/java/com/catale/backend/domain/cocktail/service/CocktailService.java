package com.catale.backend.domain.cocktail.service;


import com.catale.backend.domain.cocktail.dto.CocktailGetLikeResponseDto;
import com.catale.backend.domain.cocktail.dto.CocktailGetResponseDto;
import com.catale.backend.domain.cocktail.entity.Cocktail;
import com.catale.backend.domain.cocktail.repository.CocktailRepository;
import com.catale.backend.domain.like.dto.LikeResponseDto;
import com.catale.backend.domain.like.repository.LikeRepository;
import com.catale.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.catale.backend.domain.cocktail.dto.CocktailListResponseDto;
import com.catale.backend.domain.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CocktailService {

    private final CocktailRepository cocktailRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;

    //칵테일 전체 리스트 조회
    @Transactional
    public List<CocktailListResponseDto> getAllCocktails(Long memberId){
        //좋아요 수 많은 순서대로 리스트 가져오기
        List<CocktailListResponseDto> list = cocktailRepository.getCocktails().orElse(new ArrayList<>());
        //칵테일 마다 유저가 좋아요 했는지 유무 저장
        for(CocktailListResponseDto c : list) {
            Optional<LikeResponseDto> likeDto = likeRepository.getIsLike(memberId, c.getId());
            if(!likeDto.isEmpty()){
                c.setLike(true);
        }
        }
        return list;
    }
    //내가 좋아요 한 칵테일 리스트
    @Transactional
    public List<CocktailGetLikeResponseDto> getLikeCocktails(Long memberId){
        List<CocktailGetLikeResponseDto> list = cocktailRepository.getLikeCoctails(memberId).orElse(new ArrayList<>());
        return list;
    }

    //칵테일 상세정보 조회
    @Transactional
    public CocktailGetResponseDto getCocktailDetail(Long memberId, Long cocktailId){
        Cocktail cocktail = cocktailRepository.findById(cocktailId).orElseThrow(NullPointerException::new);
        CocktailGetResponseDto cocktailDto = new CocktailGetResponseDto(cocktail);
        //해당 칵테일의 리뷰 조회 및 dto 저장
        cocktailDto.setReviewList(reviewRepository.findByCocktailId(cocktailId).orElseThrow(NullPointerException::new));
        //해당 칵테일의 좋아요 여부 dto 등록
        Optional<LikeResponseDto> likeDto = likeRepository.getIsLike(memberId, cocktailId);
        if(!likeDto.isEmpty()){
            cocktailDto.setLike(true);
        }
        return cocktailDto;

    }

}
