package likelion.hamahama.comment.repository;

import likelion.hamahama.comment.dto.CommentRequestDto;
import likelion.hamahama.comment.entity.Comment;
import likelion.hamahama.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByTwoIds(Long userId, Long couponId);

    boolean existByIds(String userId, String couponId);

    // 전체 조회 (추가)
    Page<Comment> findAll(Pageable pageable);
    // ========= 쿠폰 이름에 포함된 키워드 찾는 메서드
   // List<Comment> findByCouponId(Long couponId);
    Page<Comment> findByCouponId(Long couponId);
}
