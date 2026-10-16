/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.persistence.CollectionTable
 *  jakarta.persistence.Column
 *  jakarta.persistence.ElementCollection
 *  jakarta.persistence.Entity
 *  jakarta.persistence.FetchType
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.Table
 */
package com.dms.auth.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true, nullable=false)
    private String email;
    @Column(nullable=false)
    private String password;
    @Column
    private String salt;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="user_roles", joinColumns={@JoinColumn(name="user_id")})
    @Column(name="role")
    private List<String> roles = new ArrayList<String>();
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="user_departments", joinColumns={@JoinColumn(name="user_id")})
    @Column(name="department")
    private List<String> departments = new ArrayList<String>();
    @Column
    private String name;
    @Column
    private String status = "active";
    @Column
    private LocalDateTime lastLogin;
    @Column
    private LocalDateTime lastLogout;
    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "active";
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSalt() {
        return this.salt;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public List<String> getDepartments() {
        return this.departments;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public LocalDateTime getLastLogin() {
        return this.lastLogin;
    }

    public LocalDateTime getLastLogout() {
        return this.lastLogout;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastLogout(LocalDateTime lastLogout) {
        this.lastLogout = lastLogout;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$salt = this.getSalt();
        String other$salt = other.getSalt();
        if (this$salt == null ? other$salt != null : !this$salt.equals(other$salt)) {
            return false;
        }
        List<String> this$roles = this.getRoles();
        List<String> other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles)) {
            return false;
        }
        List<String> this$departments = this.getDepartments();
        List<String> other$departments = other.getDepartments();
        if (this$departments == null ? other$departments != null : !((Object)this$departments).equals(other$departments)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        LocalDateTime this$lastLogin = this.getLastLogin();
        LocalDateTime other$lastLogin = other.getLastLogin();
        if (this$lastLogin == null ? other$lastLogin != null : !((Object)this$lastLogin).equals(other$lastLogin)) {
            return false;
        }
        LocalDateTime this$lastLogout = this.getLastLogout();
        LocalDateTime other$lastLogout = other.getLastLogout();
        if (this$lastLogout == null ? other$lastLogout != null : !((Object)this$lastLogout).equals(other$lastLogout)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $salt = this.getSalt();
        result = result * 59 + ($salt == null ? 43 : $salt.hashCode());
        List<String> $roles = this.getRoles();
        result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
        List<String> $departments = this.getDepartments();
        result = result * 59 + ($departments == null ? 43 : ((Object)$departments).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $lastLogin = this.getLastLogin();
        result = result * 59 + ($lastLogin == null ? 43 : ((Object)$lastLogin).hashCode());
        LocalDateTime $lastLogout = this.getLastLogout();
        result = result * 59 + ($lastLogout == null ? 43 : ((Object)$lastLogout).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "User(id=" + this.getId() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", salt=" + this.getSalt() + ", roles=" + String.valueOf(this.getRoles()) + ", departments=" + String.valueOf(this.getDepartments()) + ", name=" + this.getName() + ", status=" + this.getStatus() + ", lastLogin=" + String.valueOf(this.getLastLogin()) + ", lastLogout=" + String.valueOf(this.getLastLogout()) + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ")";
    }
}
