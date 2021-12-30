package study.datajpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

    @PersistenceContext // 스프링 컨테이너가 엔티티 매니저를 주입
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member); // 엔티티 저장
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id); // 엔티티 조회
    }

}
