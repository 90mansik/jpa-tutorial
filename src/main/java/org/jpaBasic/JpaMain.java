package org.jpaBasic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

//        insert(emf, em);
//        select(emf, em);
//        update(emf, em);
//        delete(emf, em);
        jpqlSelect(emf, em);
    }

    private static void jpqlSelect(EntityManagerFactory emf, EntityManager em) {
        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            for (Member member: result) {
                System.out.println("member.getName() = " + member.getName());
            }
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void select(EntityManagerFactory emf, EntityManager em) {
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

    private static void update(EntityManagerFactory emf, EntityManager em) {
        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void delete(EntityManagerFactory emf, EntityManager em) {
        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            Member findMember = em.find(Member.class, 1L);

            em.remove(findMember);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    public static void insert(EntityManagerFactory emf, EntityManager em) {
        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

}
