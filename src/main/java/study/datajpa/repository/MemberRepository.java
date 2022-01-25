package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

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

    // jpql을 안쓰고도 n+1 문제해결
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // jpql에 엔티티 그래프를 써서 fetch join 가능
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 이렇게도 엔티티 그래프 적용가능
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //@EntityGraph를 사용하면 fetch join을 쉽게할 수 있다!

    // JPA 쿼리 힌트 -> 변경감지 기능을 끈다. (읽기용으로만 사용할때 성능 최적화를 위해 쓴다.)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select for update 날아가게 설정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    //List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    // List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    @Query(value = "select * from member where username = ?", nativeQuery = true)//네이티브 쿼리
    Member findByNativeQuery(String username);

    //DTO 편하게 뽑기
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}