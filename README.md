## JPA 설정 하기

**persistenc.xml 생성**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             version="2.1">
    <persistence-unit name="hello">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <!-- 옵션 -->
            <!-- 콘솔에 하이버네이트가 실행하는 SQL문 출력 -->
            <property name="hibernate.show_sql" value="true"/>
            <!-- SQL 출력 시 보기 쉽게 정렬 -->
            <property name="hibernate.format_sql" value="true"/>
            <!-- 쿼리 출력 시 주석(comments)도 함께 출력 -->
            <property name="hibernate.use_sql_comments" value="true"/>
            <!-- JPA 표준에 맞춘 새로운 키 생성 전략 사용 -->
            <property name="hibernate.id.new_generator_mappings" value="true"/>
            <!-- 애플리케이션 실행 시점에 데이터베이스 테이블 자동 생성 -->
            <!--            <property name="hibernate.hbm2ddl.auto" value="create"/>-->
            <!-- 이름 매핑 전략 설정 - 자바의 카멜 표기법을 테이블의 언더스코어 표기법으로 매핑
             ex) lastModifiedDate -> last_modified_date -->

        </properties>
    </persistence-unit>
</persistence>
```

- JPA는 기본적으로 persistance.xml에서 설정 정보를 관리.
    - resource/META-INF/persistence.xml에 위치 시  별도 설정 없이 JPA가 인식
- <persistence-unit>
    - name에 unit의 고유한 이름을 설정, 일반적으로 연결할 데이터베이스당 하나의 persistence unit을 등록한
- <properties> 아래에 JPA 표준 속성과 하이버네이트 속성을 설정
- 필수 속성
    - javax.persistence.*  :JPA 표준 속성
        - 데이터베이스의 연결정보를 설정
    - hibernate.* : 하이버네이트 전용 속성
        - 데이터베이스 방언(dialect)를 설정
        - 데이트베이스 방언(dialect) : SQL별로 표준을 지키지 않은 데이터베이스만의 고유한 기능/문법
        - 하이버네이트는 40가지 이상의 데이터베이스 방언을 지원

      ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/455af4d1-3237-4534-959b-0926147dfc04/Untitled.png)


### JPA 구동방식

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/aa2b7e72-13ba-47a9-b2a5-74b0cd68f673/Untitled.png)

- EntityManagerFactory는 하나만 생성해서 애플리케이션 전체에서 공유
- EntityManager는 쓰레드간에 공유하면 안된다.
    - 동시성 문제가 발생
- JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
-

### **@Entity를 이용한 객체 생성**

```java
@Entity
public class Member {
    @Id
    private Long id;
    private String name;
		
		// Getter, Setter 생성
}

/*
Hibernate: 
	insert 
        into
            Member
            (name, id) 
        values
            (?, ?)
*/
```

- @Entity
    - DB 테이블에 대응하는 하나의 클래스
    - JPA가 관리할 수 있도록 인식하게 해주는 애노테이션
- @ID
    - 객체의 Primaray Key
    - JPA는 @id를 통해 객체를 구분

### 회원 단건 조회

```java
public class JpaMain {
    public static void main(String[] args) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
            EntityManager em = emf.createEntityManager();
                  EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
            tx.begin();         // 트랜잭션 시작
    
            try {
                Member findMember = em.find(Member.class, 1L);
                System.out.println("findMember.id = " + findMember.getId());
                System.out.println("findMember.name = " + findMember.getName());
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            } finally {
                em.close();
            }
            emf.close();
        }
}    
```
- Persistenc를 통해 EntityManagerFactory를 생성
    - persistenc.xml에서 설정한 persistence-unit name의 값을 불러온다.
- EntityManager 생성
- EntityTransaction
    - 데이터를 변경하는 모든 작업은 반드시 트랜잭션 안에서 이루어져 함.
- EntityManager의 find를 통해 하나의 회원 정보를 가져 옴.
- Update시에는 setter에 set을 하게 되면 데이터가 변경 되게 된다.
    - 변경 사항은 트랜잭션을 commit하는 시점에 체크 하며, 변경사항이 있으면 트랜직션 직전에 업데이트 쿼리를 만들어 날리고 트랜잭션이 commit 됨


# 영속성 관리

### 영속성

- 데이터를 생성한 프로그램이 종료되어도 사라지지 않는 데이터의 특성
- 영속성을 갖지 않으면 데이터는 메모리에서만 존재하게 되고 프로그램이 종료되면 해당 데이터는 모두 사라지게 되므로, 데이터를 파일이나 DB에 영구 저장함으로써 데이터에 영속성을 부여

### 엔티티의 생명 주기

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b78b089e-6290-44bc-ac18-58cc5baee0f0/Untitled.png)

```java
public class Main(){
    public static void persistence() {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
    
            tx.begin();
    
            try {
                // 비영속
                Member member = new Member();
                member.setId(101L);
                member.setName("HelloJPA");
    
                // 영속
                System.out.println("==== BEFORE =====");
                em.persist(member);
                System.out.println("==== AFTER =====");
                            
                            // 준영속 상태
                            em.detach(member);
    
                Member findMember = em.find(Member.class, 101L);
                Member findMember2 = em.find(Member.class, 101L);
    
                System.out.println("findMember.id = " + findMember.getId());
                System.out.println("findMember.name = " + findMember.getName());
                            // 객체를 상태한 상태
                            em.remove(member)
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            } finally {
                em.close();
            }
    
            emf.close();
        }
}
```

- 비영속(new/transient)
    - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속( managed)
    - 영속성 컨텍스트에 관리되는  상태
    - em.persist(member) 객체를 저장한 상태
    - 엔티티를 저장하는 INSERT 쿼리문이 생성 되었지만, 아직 DB에게 전달되지 않고 쿼리문 저장소에 보관
- 준영속(datached)
    - 영속성 컨텍스트에 저장되었다가 **분리**된 상태
- 삭제 (removed)
    - 삭제 상태는 엔티티를 영속성 컨텍스트에서 관리하지 않게 되고, 해당 엔티티를 DB에서 삭제하는 DELETE 쿼리문을 보관하게 됩니다

## 영속성 컨텍스트의 이점

### 1차캐시

- EntityManager가 관리하는 영속성 컨텍스트 내부에 있는 첫 번째 캐시입니다.
- 조회 동작 방식
    - 1차 캐시에 데이터가 이미 있는지 확인하고, 데이터가 있으면 데이터를 가져온다.
    - 1차 캐시에 데이터가 없다면, 데이터 베이스에 데이터를 요청 한다.
    - 데이터베이스에서 받은 데이터를 다음에 재사용할 수 있도록 1차 캐시에 저장
- 쓰기 동작 방식
    - 데이터가 변경되면 즉시 1차캐시에 반영
    - 변경 사항이 지연 SQL 저장소에 저장
    - Transaction이 commit되면 Flush가 발생
    - 지연 SQL 저장소에 있는 SQL문을 DB에 요청

### 동일성 보장(identity) 보장

- 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭
  션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
