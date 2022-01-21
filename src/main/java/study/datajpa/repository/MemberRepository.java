package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); // 조건이 추가돼서 이름이 너무 길어지면 다른 방법을 사용

    //spring data jpa를 사용하면 이렇게 NamedQuery를 쉽게 사용할 수 있다.
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username); //이렇게 메서드 네임을 관례에 맞추면 @Query 생략 가능!

    //이렇게 실무에서 많이쓴다. 메서드명을 간략하게 가져갈 수 있음. 복잡한 쿼리 실행가능
    //jpql에 오타가 나면 컴파일 오류가 뜬다는 장점이 있다.
    //아래는 이름이 없는 NamedQuery다. NamedQuery는 정적 쿼리이므로 jpql 문법 오류가 파싱 과정에서 감지된다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // 참고로 동적쿼리는 queryDsl을 쓰는것이 좋다.
}