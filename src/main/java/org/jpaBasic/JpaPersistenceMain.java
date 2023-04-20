package org.jpaBasic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaPersistenceMain {

    public static void main(String[] args) {
        persistenceCash();
        persistenceIdentity();
        persistenceFlush();

    }

    // 영속성 1차 캐시
    public static void persistenceCash() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // 비영속
            BasicMember member = new BasicMember();
            member.setId(101L);
            member.setUsername("HelloJPA");

            // 영속
            System.out.println("==== BEFORE =====");
            em.persist(member);
            System.out.println("==== AFTER =====");

            BasicMember findMember = em.find(BasicMember.class, 101L);
            BasicMember findMember2 = em.find(BasicMember.class, 101L);

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

    // 영속 엔티티의 동일성 보장
    public static void persistenceIdentity() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            // 영속
            BasicMember findMember = em.find(BasicMember.class, 101L);
            BasicMember findMember2 = em.find(BasicMember.class, 101L);

            System.out.println("result = " + (findMember == findMember2));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    public static void persistenceFlush() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            BasicMember findMember = em.find(BasicMember.class, 101L);
            findMember.setUsername("change");

            System.out.println("==============");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
