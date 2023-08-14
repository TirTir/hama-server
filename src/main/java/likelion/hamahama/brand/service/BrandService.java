package likelion.hamahama.brand.service;

import likelion.hamahama.brand.dto.BrandDto;
import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.brand.entity.BrandLike;
import likelion.hamahama.brand.repository.BrandLikeRepsitory;
import likelion.hamahama.brand.repository.BrandRepository;
import likelion.hamahama.coupon.entity.enums.Category;
import likelion.hamahama.user.entity.User;
import likelion.hamahama.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// 0814 @Autowired 지우고 requierdArgeConstructor로 변경
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final BrandLikeRepsitory brandLikeRepsitory;

    public List<Brand> findAll_brand(){
        return brandRepository.findAll();
    }

    public Brand findBrandById(long theId){
        return brandRepository.findById(theId);
    }

    // 카테고리에 맞는 브랜드 불러오기
    public List<Brand> findByCategory(Category category){
        return brandRepository.findByCategory(category);
    }

    // 브랜드이름에서 키워드 검색 후 결과 리스트 출력
    public List<Brand> findByKeyword(String keyword){
        return brandRepository.findByBrandNameContaining(keyword);
    }

    // DTO 받아와서 브랜드 저장하기
    @Transactional
    public void saveBrand(BrandDto brandDto){
        Brand tempBrand = new Brand(brandDto.getCategory(),
                brandDto.getBrandName(),
                brandDto.getBrandImgUrl());

        brandRepository.save(tempBrand);
    }

    // DTO를 받아와서 브랜드 업데이트
    @Transactional
    public void updateBrand(BrandDto brandDto, long brandId){

        Brand tempBrand = brandRepository.findById(brandId);

        tempBrand.setBrandName(brandDto.getBrandName());
        tempBrand.setCategory(brandDto.getCategory());
        tempBrand.setBrandImgUrl(brandDto.getBrandImgUrl());

        brandRepository.save(tempBrand);
    }

    // 브랜드 ID 기반으로 삭제
    @Transactional
    public void deleteById(long theId){
        brandRepository.deleteById(theId);
    }

    //===============================
    // 브랜드 즐겨찾기
    @Transactional
    public void createBrandFavorite(User user, Brand brand){
        BrandLike createdBrandLike = new BrandLike(user, brand);
        brandLikeRepsitory.save(createdBrandLike);
    }
    @Transactional
    public void deleteBrandFavorite(User user, Brand brand){
        BrandLike brandLike = brandLikeRepsitory.findOneByUserAndBrand(user,brand);
        brandLike.disLike();
        brandLikeRepsitory.delete(brandLike);

    }
    // ================ 마이페이지에서 브랜드 즐겨찾기 불러오기 =======================
    /** 여기서는 브랜드아이디로 받아와도 되지 않나?*/
    public Page<BrandDto> getLikedBrand(Long userId, Pageable pageable){
        User user = userRepository.findById(userId).get();
        List<BrandLike> brandLikes = brandLikeRepsitory.findByUser(user);
        Page<Brand> brands = brandRepository.findAllByLikeUsersIn(brandLikes, pageable);
        Page<BrandDto> brandDto = brands.map(c-> new BrandDto(c));
        return brandDto;
    }
}
