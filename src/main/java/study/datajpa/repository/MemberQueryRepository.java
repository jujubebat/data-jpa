package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository { // 이런식으로 용도에 따라 레포지토리 클래스를 분리해서 사용하는 방법도 있다.

    private final EntityManager em;

    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
