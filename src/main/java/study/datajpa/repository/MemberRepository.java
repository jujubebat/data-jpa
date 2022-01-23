package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //SQL in절
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalListByUsername(String username);

    // 아래처럼 하면 totalcount용 쿼리를 따로 정할 수 있다. 카운트 쿼리에서는 굳이 조인 안해도 되므로 성능 효율을 높일 수 있다.
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //Slice<Member> findByAgeSlice(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"} ) // jpql을 안쓰고도 n+1 문제해결
    List<Member> findAll();


    @EntityGraph(attributePaths = {"team"} ) // jpql에 엔티티 그래프를 써서 fetch join 가능
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"} ) // 이렇게도 엔티티 그래프 적용가능
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //@EntityGraph를 사용하면 fetch join을 쉽게할 수 있다.
}