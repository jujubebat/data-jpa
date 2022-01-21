package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); // 조건이 추가돼서 이름이 너무 길어지면 다른 방법을 사용

    @Query(name = "Member.findByUsername") //spring data jpa를 사용하면 이렇게 NamedQuery를 쉽게 사용할 수 있다.
    List<Member> findByUsername(@Param("username") String username); //이렇게 메서드 네임을 관례에 맞추면 @Query 생략 가능!

}