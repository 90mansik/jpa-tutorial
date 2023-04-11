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
- persistence-unit
    - name에 unit의 고유한 이름을 설정, 일반적으로 연결할 데이터베이스당 하나의 persistence unit을 등록
- properties 아래에 JPA 표준 속성과 하이버네이트 속성을 설정
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
                            em.remove(member);
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

## Entity 매핑

### 객체와 테이블 매핑 하려면?

- @Entity, @Table 애노테이션을 붙여준다.

### @Entity

- JPA가 @Entity가 붙은 클래스를 관리 하게 되고, 테이블과 매핑 시켜 준다.
- Entity 사용 시 주의 사항
  - 기본 생성자가 필수 ( public, protected 생성자 )
    - 자바는 기본 생성자를 만들지 않으면 빈 생성자를 자동으로 생성한다.
    - 그러나 별도의 생성자를 정의할 경우 기본 생성자가 만들어지지 않는다.
    - 그럴 땐 반드시 기본 생성자를 명시적으로 정의해야 한다.
  - final 클래스, interface, innser 클래스에는 사용 불가 하다.
- 속성
  - name : Jpa에서 사용할 Entity의 이름을 부여, 디폴트로 클래스 명을 사용한다.

### @Table

- Entity와 매핑할 테이블을 지정한다.
- 속성
  - name : 매핑할 테이블 이름, 디폴트로 엔티티 이름을 사용
  - catelog : 데이터 베이스의 catalog 매핑
    - 데이터베이스 catelog : DBA의 도구로서 데이터베이스에 저장되어 있는 모든 데이터 개체들에 대한 정의나 명세에 대한 정보를 수록한 시스템 테이블
  - schema : 데이터베이스 schema 매핑
    - 데이터베이스 schema : 데이터베이스의 구조와 제약조건에 관한 전반적인 명세를 기술한 것
  - uniqueConstraints(DDL)
    - DDL 생성 시에 유니크 제약 조건 생성

## 데이터베이스 스키마 자동 생성

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             version="2.1">
    <persistence-unit name="hello">
        <properties>
					<property name="hibernate.hbm2ddl.auto" value="create"/>
        </properties>
    </persistence-unit>
</persistence>
```

- 위의 [hibernate.hbm2ddl.auto](http://hibernate.hbm2ddl.auto) 옵션을 사용하여 애플리케이션 실행 시점에  DDL문을 자동 생성 가능하다.
- 이때 JPA는 데이터베이스 방언을 활용해서 데이터베이스에 적절한 DDL문을 만들어준다.
- 생성된 DDL문은 로컬 테스트 및 개발 장비에서만 사용할 것을 권장하고, 운영서버에 사용할 경우에는 적절히 다듬어서 사용 해야 한다.
- hibernate.hbm2ddl.auto 옵션
  - create : 기존테이블 삭제 후 다시 생성 (DROP + CREATE)
  - create-drop : create 같으나 종료 시점에 테이블 DROP
  - update : 변경분만 반영
    - 운영 DB에 사용 시 테이블이 락이 걸려 문제가 될 수 있다. 운영 DB에는 사용 X
  - validate : 엔티티와 테이블이 정상 매핑되었는지만 확인
  - none : 사용하지 않음.
    - 위4가지 옵션을 사용하지 않으면 아무 옵션을 입력해도 none으로 처리 되지만 개발자의 편의성을 위해 표
- hibernate.hbm2ddl.auto 사용 전략
  - 개발 초기 단계(로컬) : create, update
  - 테스트 서버 : update, validate
  - 스테이징, 운영 서버 : validate, none
