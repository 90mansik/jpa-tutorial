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

        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            BasicMember member = new BasicMember();
            member.setId(1L);
            member.setUsername("A");
            member.setRoleType(RoleType.USER);

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void jpqlSelect(EntityManagerFactory emf, EntityManager em) {
        EntityTransaction tx = em.getTransaction();     // 트랜잭션 얻기
        tx.begin();         // 트랜잭션 시작

        try {
            List<BasicMember> result = em.createQuery("select m from BasicMember as m", BasicMember.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            for (BasicMember member: result) {
                System.out.println("member.getName() = " + member.getUsername());
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
            BasicMember findMember = em.find(BasicMember.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getUsername());
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
            BasicMember findMember = em.find(BasicMember.class, 1L);
            findMember.setUsername("HelloJPA");

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
            BasicMember findMember = em.find(BasicMember.class, 1L);

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
            BasicMember member = new BasicMember();
            member.setId(1L);
            member.setUsername("HelloA");

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
