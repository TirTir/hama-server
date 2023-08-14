package likelion.hamahama.brand.controller;

import likelion.hamahama.brand.dto.BrandDto;
import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.brand.entity.BrandLike;
import likelion.hamahama.brand.repository.BrandLikeRepsitory;
import likelion.hamahama.brand.repository.BrandRepository;
import likelion.hamahama.brand.service.BrandService;
import likelion.hamahama.common.CreateResponseMessage;
import likelion.hamahama.coupon.entity.enums.Category;
import likelion.hamahama.user.entity.User;
import likelion.hamahama.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final  BrandLikeRepsitory brandLikeRepsitory;

    // 모든 브랜드 조회
    @GetMapping("/brandList")
    public List<Brand> findAll_brand() {
        return brandService.findAll_brand();
    }

    // 브랜드 상세 조회
    @GetMapping("/{brandId}")
    public BrandDto getBrand(@PathVariable long brandId){
        BrandDto theBrandDTO = new BrandDto(brandService.findBrandById(brandId));

        if(theBrandDTO == null){
            throw new RuntimeException("브랜드가 발견 되지 않았습니다 - " + brandId);
        }

        return theBrandDTO;
    }
    // ============== 브랜드 즐겨찾기 (마이페이지) ================
    @PostMapping("/mypage/{userId}/{brandId}/edit")
    public CreateResponseMessage likeBrand(@PathVariable("userId") Long userId, @PathVariable("brandId") Long brandId){
        Brand brand = brandRepository.findById(brandId).get();
        User user = userRepository.findById(userId).get();
        BrandLike brandLike = brandLikeRepsitory.findOneByUserAndBrand(user, brand);
        if(brandLike == null ){
            brandService.createBrandFavorite(user, brand);
        }else brandService.deleteBrandFavorite(user, brand);
        return new CreateResponseMessage((long) 200, "좋아요 성공");
    }
    // ============== 카테고리에 맞는 브랜드 찾기 =============
    @GetMapping("/{category}")
    public List<Brand> brandByCategory(Category category){
        return brandService.findByCategory(category);
    }

    // ============= 브랜드 검색 시 브랜드 출력 ==============
    @GetMapping("/search/brand")
    public List<Brand> findBrandByKeyword(@RequestParam String keyword){
        List<Brand> brandlist = brandService.findByKeyword(keyword);
        return brandlist;
    }

}
