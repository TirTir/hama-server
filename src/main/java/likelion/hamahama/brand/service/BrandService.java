package likelion.hamahama.brand.service;

import likelion.hamahama.brand.dto.BrandDto;
import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.brand.repository.BrandRepository;
import likelion.hamahama.coupon.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> findAll_brand(){
        return brandRepository.findAll();
    }

    public Optional<Brand> findBrandById(Long brandId){

        return brandRepository.findById(brandId);
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
    public void updateBrand(BrandDto brandDto, Long brandId){

        Optional<Brand> tempBrand = brandRepository.findById(brandId);

        tempBrand.get().setBrandName(brandDto.getBrandName());
        tempBrand.get().setCategory(brandDto.getCategory());
        tempBrand.get().setBrandImgUrl(brandDto.getBrandImgUrl());

        brandRepository.save(tempBrand.get());
    }

    // 브랜드 ID 기반으로 삭제
    @Transactional
    public void deleteById(long theId){
        brandRepository.deleteById(theId);
    }
}
