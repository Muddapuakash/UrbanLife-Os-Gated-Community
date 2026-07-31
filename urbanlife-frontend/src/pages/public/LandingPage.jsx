import { Link } from 'react-router-dom';
import { useEffect, useRef, useState } from 'react';
import {
  Building2, ShieldCheck, UserCheck, Package,
  Wrench, ShieldAlert, Award, ArrowRight, CheckCircle2,
  Bell, Vote, Calendar, CreditCard, Users, Star, Zap, Lock
} from 'lucide-react';

/* ─── Floating Particle Canvas ─── */
function ParticleCanvas() {
  const canvasRef = useRef(null);
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    const particles = Array.from({ length: 60 }, () => ({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 2 + 0.5,
      dx: (Math.random() - 0.5) * 0.4,
      dy: (Math.random() - 0.5) * 0.4,
      alpha: Math.random() * 0.5 + 0.1,
    }));

    let raf;
    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      particles.forEach(p => {
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(16,185,129,${p.alpha})`;
        ctx.fill();
        p.x += p.dx;
        p.y += p.dy;
        if (p.x < 0 || p.x > canvas.width) p.dx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.dy *= -1;
      });
      raf = requestAnimationFrame(draw);
    };
    draw();
    const onResize = () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; };
    window.addEventListener('resize', onResize);
    return () => { cancelAnimationFrame(raf); window.removeEventListener('resize', onResize); };
  }, []);
  return <canvas ref={canvasRef} style={{ position: 'absolute', top: 0, left: 0, pointerEvents: 'none', zIndex: 0 }} />;
}

/* ─── Animated Counter ─── */
function Counter({ target, suffix = '' }) {
  const [count, setCount] = useState(0);
  const ref = useRef(null);
  useEffect(() => {
    const observer = new IntersectionObserver(([e]) => {
      if (!e.isIntersecting) return;
      let start = 0;
      const step = target / 60;
      const timer = setInterval(() => {
        start += step;
        if (start >= target) { setCount(target); clearInterval(timer); }
        else setCount(Math.floor(start));
      }, 20);
      observer.disconnect();
    });
    if (ref.current) observer.observe(ref.current);
    return () => observer.disconnect();
  }, [target]);
  return <span ref={ref}>{count}{suffix}</span>;
}

const features = [
  { icon: UserCheck, color: '#10b981', bg: 'rgba(16,185,129,0.12)', title: 'Smart Gate Access', desc: 'Pre-approve guests with 6-digit digital pass codes. Security checks in visitors instantly.' },
  { icon: ShieldAlert, color: '#f43f5e', bg: 'rgba(244,63,94,0.12)', title: 'Emergency SOS', desc: '1-click emergency broadcast reaches security & staff dashboards in under 3 seconds.' },
  { icon: Package, color: '#3b82f6', bg: 'rgba(59,130,246,0.12)', title: 'Parcel Desk', desc: 'Security logs Amazon/Swiggy parcels and alerts residents with instant pickup codes.' },
  { icon: Wrench, color: '#f59e0b', bg: 'rgba(245,158,11,0.12)', title: 'Helpdesk & Complaints', desc: 'Raise repair tickets with real-time status tracking and staff assignment.' },
  { icon: Bell, color: '#a78bfa', bg: 'rgba(167,139,250,0.12)', title: 'Notice Board', desc: 'Broadcast bulletins & emergency advisories to the whole community or specific blocks.' },
  { icon: Vote, color: '#34d399', bg: 'rgba(52,211,153,0.12)', title: 'Community Polls', desc: 'Empower residents with 1-click voting on society decisions with live % bars.' },
  { icon: Calendar, color: '#fb923c', bg: 'rgba(251,146,60,0.12)', title: 'Events & RSVP', desc: 'Schedule festivals, GBM, or club events with RSVP tracking & venue details.' },
  { icon: CreditCard, color: '#38bdf8', bg: 'rgba(56,189,248,0.12)', title: 'Billing & Payments', desc: 'View invoices and clear monthly society maintenance bills securely online.' },
  { icon: Award, color: '#c084fc', bg: 'rgba(192,132,252,0.12)', title: 'Amenity Bookings', desc: 'Reserve society clubhouse, gym, and pool slots seamlessly from the portal.' },
];

const roles = [
  { icon: '🏠', label: 'Resident', color: '#10b981', desc: 'Manage your flat, pay bills, raise complaints & vote on society matters.' },
  { icon: '🛡️', label: 'Security Guard', color: '#3b82f6', desc: 'Gate check-in/out, parcel desk, visitor log & emergency alerts.' },
  { icon: '⚙️', label: 'Admin', color: '#f59e0b', desc: 'Full society oversight — residents, notices, events & finance.' },
  { icon: '🔧', label: 'Maintenance Staff', color: '#f43f5e', desc: 'View assigned work orders, update status & resolve complaints.' },
  { icon: '👑', label: 'Super Admin', color: '#a78bfa', desc: 'Multi-community management with complete platform control.' },
];

const stats = [
  { value: 500, suffix: '+', label: 'Communities Managed' },
  { value: 12000, suffix: '+', label: 'Residents Connected' },
  { value: 98, suffix: '%', label: 'Uptime Guaranteed' },
  { value: 19, suffix: ' Modules', label: 'Fully Integrated' },
];

export default function LandingPage() {
  const [scrolled, setScrolled] = useState(false);
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 60);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  return (
    <div style={{ backgroundColor: '#0b0f19', minHeight: '100vh', color: '#f1f5f9', fontFamily: "'Plus Jakarta Sans', sans-serif", overflowX: 'hidden' }}>

      {/* ── Sticky Navbar ── */}
      <nav style={{
        position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '16px 48px',
        backgroundColor: scrolled ? 'rgba(11,15,25,0.92)' : 'transparent',
        backdropFilter: scrolled ? 'blur(20px)' : 'none',
        borderBottom: scrolled ? '1px solid rgba(30,41,59,0.8)' : '1px solid transparent',
        transition: 'all 0.3s ease',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{
            width: '42px', height: '42px',
            background: 'linear-gradient(135deg, #10b981, #059669)',
            borderRadius: '12px',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 800, color: '#fff', fontSize: '1.3rem',
            boxShadow: '0 0 20px rgba(16,185,129,0.4)'
          }}>U</div>
          <span style={{ fontSize: '1.5rem', fontWeight: 800, letterSpacing: '-0.5px' }}>UrbanLife</span>
          <span style={{
            fontSize: '0.7rem', fontWeight: 700, padding: '2px 8px',
            background: 'rgba(16,185,129,0.15)', color: '#10b981',
            borderRadius: '20px', border: '1px solid rgba(16,185,129,0.3)'
          }}>Community OS</span>
        </div>

        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <Link to="/login" style={{
            padding: '10px 24px', borderRadius: '10px', fontWeight: 600, fontSize: '0.9rem',
            color: '#f1f5f9', border: '1px solid rgba(255,255,255,0.15)',
            background: 'rgba(255,255,255,0.05)', textDecoration: 'none',
            transition: 'all 0.2s',
          }}
            onMouseEnter={e => e.target.style.background = 'rgba(255,255,255,0.1)'}
            onMouseLeave={e => e.target.style.background = 'rgba(255,255,255,0.05)'}
          >Sign In</Link>
          <Link to="/register" style={{
            padding: '10px 24px', borderRadius: '10px', fontWeight: 700, fontSize: '0.9rem',
            color: '#fff', textDecoration: 'none',
            background: 'linear-gradient(135deg, #10b981, #059669)',
            boxShadow: '0 4px 15px rgba(16,185,129,0.4)',
            transition: 'all 0.2s',
          }}>Register →</Link>
        </div>
      </nav>

      {/* ── Hero Section ── */}
      <section style={{ position: 'relative', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
        <ParticleCanvas />

        {/* Glowing orbs */}
        <div style={{ position: 'absolute', top: '15%', left: '8%', width: '500px', height: '500px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(16,185,129,0.18) 0%, transparent 70%)', filter: 'blur(60px)', pointerEvents: 'none' }} />
        <div style={{ position: 'absolute', bottom: '10%', right: '5%', width: '400px', height: '400px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(59,130,246,0.15) 0%, transparent 70%)', filter: 'blur(60px)', pointerEvents: 'none' }} />
        <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', width: '800px', height: '800px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(16,185,129,0.05) 0%, transparent 70%)', filter: 'blur(40px)', pointerEvents: 'none' }} />

        <div style={{ position: 'relative', zIndex: 1, maxWidth: '900px', margin: '0 auto', padding: '120px 24px 80px', textAlign: 'center' }}>
          {/* Badge */}
          <div style={{
            display: 'inline-flex', alignItems: 'center', gap: '8px',
            padding: '6px 18px', borderRadius: '30px', marginBottom: '28px',
            background: 'rgba(16,185,129,0.1)', color: '#10b981',
            border: '1px solid rgba(16,185,129,0.3)', fontSize: '0.85rem', fontWeight: 700,
            backdropFilter: 'blur(10px)',
          }}>
            <Zap size={14} fill="#10b981" /> Next-Generation Residential Society Platform
          </div>

          <h1 style={{ fontSize: 'clamp(2.6rem, 5vw, 4.2rem)', fontWeight: 900, lineHeight: 1.1, marginBottom: '24px', letterSpacing: '-2px' }}>
            The Complete{' '}
            <span style={{
              background: 'linear-gradient(135deg, #10b981 0%, #3b82f6 100%)',
              WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
            }}>Operating System</span>
            <br />for Modern Gated Communities
          </h1>

          <p style={{ fontSize: '1.15rem', color: '#94a3b8', maxWidth: '680px', margin: '0 auto 40px', lineHeight: 1.7 }}>
            UrbanLife connects <strong style={{ color: '#f1f5f9' }}>Residents, Admins, Security Guards</strong> and <strong style={{ color: '#f1f5f9' }}>Maintenance Staff</strong> on one intelligent platform — from gate entry to community voting.
          </p>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', flexWrap: 'wrap' }}>
            <Link to="/register" style={{
              display: 'inline-flex', alignItems: 'center', gap: '8px',
              padding: '16px 36px', borderRadius: '14px', fontWeight: 800, fontSize: '1rem',
              color: '#fff', textDecoration: 'none',
              background: 'linear-gradient(135deg, #10b981, #059669)',
              boxShadow: '0 8px 30px rgba(16,185,129,0.5)',
              transition: 'transform 0.2s, box-shadow 0.2s',
            }}
              onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 12px 40px rgba(16,185,129,0.6)'; }}
              onMouseLeave={e => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 8px 30px rgba(16,185,129,0.5)'; }}
            >
              Get Started Free <ArrowRight size={18} />
            </Link>
            <Link to="/login" style={{
              display: 'inline-flex', alignItems: 'center', gap: '8px',
              padding: '16px 36px', borderRadius: '14px', fontWeight: 700, fontSize: '1rem',
              color: '#f1f5f9', textDecoration: 'none',
              background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.15)',
              backdropFilter: 'blur(10px)', transition: 'all 0.2s',
            }}
              onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
              onMouseLeave={e => e.currentTarget.style.background = 'rgba(255,255,255,0.06)'}
            >
              <Lock size={16} /> Access Portal
            </Link>
          </div>

          {/* Trust badges */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: '24px', marginTop: '48px', flexWrap: 'wrap' }}>
            {['JWT Secured', 'Role-Based Access', 'Real-Time Updates', 'Spring Boot Powered'].map(t => (
              <div key={t} style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#64748b', fontSize: '0.82rem', fontWeight: 600 }}>
                <CheckCircle2 size={14} color="#10b981" /> {t}
              </div>
            ))}
          </div>
        </div>

        {/* Scroll arrow */}
        <div style={{ position: 'absolute', bottom: '32px', left: '50%', transform: 'translateX(-50%)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px', color: '#475569', fontSize: '0.75rem' }}>
          <span>Scroll to explore</span>
          <div style={{ width: '20px', height: '32px', border: '2px solid #334155', borderRadius: '10px', display: 'flex', justifyContent: 'center', paddingTop: '4px' }}>
            <div style={{ width: '4px', height: '8px', background: '#10b981', borderRadius: '2px', animation: 'bounce 1.5s infinite' }} />
          </div>
        </div>
      </section>

      {/* ── Stats Banner ── */}
      <section style={{ background: 'linear-gradient(135deg, rgba(16,185,129,0.1) 0%, rgba(59,130,246,0.08) 100%)', borderTop: '1px solid rgba(16,185,129,0.2)', borderBottom: '1px solid rgba(16,185,129,0.2)', padding: '48px 24px' }}>
        <div style={{ maxWidth: '1100px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '32px', textAlign: 'center' }}>
          {stats.map(s => (
            <div key={s.label}>
              <div style={{ fontSize: '2.8rem', fontWeight: 900, background: 'linear-gradient(135deg, #10b981, #3b82f6)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                <Counter target={s.value} suffix={s.suffix} />
              </div>
              <div style={{ color: '#64748b', fontWeight: 600, fontSize: '0.9rem', marginTop: '4px' }}>{s.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* ── Features Grid ── */}
      <section style={{ maxWidth: '1200px', margin: '0 auto', padding: '100px 24px' }}>
        <div style={{ textAlign: 'center', marginBottom: '64px' }}>
          <div style={{ display: 'inline-block', padding: '4px 16px', background: 'rgba(16,185,129,0.1)', color: '#10b981', borderRadius: '20px', fontSize: '0.8rem', fontWeight: 700, border: '1px solid rgba(16,185,129,0.25)', marginBottom: '16px', letterSpacing: '1px', textTransform: 'uppercase' }}>
            Platform Capabilities
          </div>
          <h2 style={{ fontSize: 'clamp(1.8rem, 3vw, 2.8rem)', fontWeight: 900, letterSpacing: '-1px', marginBottom: '16px' }}>
            Everything Your Society Needs,<br /><span style={{ color: '#10b981' }}>All in One Place</span>
          </h2>
          <p style={{ color: '#64748b', fontSize: '1rem', maxWidth: '560px', margin: '0 auto' }}>
            19 fully integrated modules covering every touchpoint of residential society management.
          </p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
          {features.map((f, i) => {
            const Icon = f.icon;
            return (
              <div key={i} style={{
                background: 'rgba(19,27,46,0.8)',
                border: '1px solid rgba(30,41,59,0.8)',
                borderRadius: '18px',
                padding: '28px',
                backdropFilter: 'blur(10px)',
                transition: 'transform 0.25s, border-color 0.25s, box-shadow 0.25s',
                cursor: 'default',
              }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-6px)'; e.currentTarget.style.borderColor = f.color + '55'; e.currentTarget.style.boxShadow = `0 20px 50px ${f.color}22`; }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.borderColor = 'rgba(30,41,59,0.8)'; e.currentTarget.style.boxShadow = 'none'; }}
              >
                <div style={{ width: '52px', height: '52px', borderRadius: '14px', background: f.bg, display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '18px' }}>
                  <Icon size={26} color={f.color} />
                </div>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 800, marginBottom: '8px' }}>{f.title}</h4>
                <p style={{ color: '#64748b', fontSize: '0.88rem', lineHeight: 1.65 }}>{f.desc}</p>
              </div>
            );
          })}
        </div>
      </section>

      {/* ── Roles Section ── */}
      <section style={{ background: 'linear-gradient(180deg, transparent 0%, rgba(16,185,129,0.04) 50%, transparent 100%)', padding: '80px 24px' }}>
        <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', marginBottom: '56px' }}>
            <div style={{ display: 'inline-block', padding: '4px 16px', background: 'rgba(59,130,246,0.1)', color: '#3b82f6', borderRadius: '20px', fontSize: '0.8rem', fontWeight: 700, border: '1px solid rgba(59,130,246,0.25)', marginBottom: '16px', letterSpacing: '1px', textTransform: 'uppercase' }}>
              Role-Based Access
            </div>
            <h2 style={{ fontSize: 'clamp(1.8rem, 3vw, 2.6rem)', fontWeight: 900, letterSpacing: '-1px' }}>
              One Platform, <span style={{ color: '#3b82f6' }}>Five Smart Roles</span>
            </h2>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px' }}>
            {roles.map((r, i) => (
              <div key={i} style={{
                background: `linear-gradient(135deg, rgba(19,27,46,0.9), rgba(19,27,46,0.6))`,
                border: `1px solid ${r.color}33`,
                borderRadius: '18px', padding: '28px 20px', textAlign: 'center',
                backdropFilter: 'blur(10px)',
                transition: 'transform 0.25s, box-shadow 0.25s',
              }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-4px)'; e.currentTarget.style.boxShadow = `0 16px 40px ${r.color}33`; }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = 'none'; }}
              >
                <div style={{ fontSize: '2.4rem', marginBottom: '12px' }}>{r.icon}</div>
                <div style={{ fontWeight: 800, fontSize: '1rem', color: r.color, marginBottom: '8px' }}>{r.label}</div>
                <p style={{ color: '#64748b', fontSize: '0.82rem', lineHeight: 1.55 }}>{r.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA Section ── */}
      <section style={{ padding: '100px 24px', textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', width: '600px', height: '400px', background: 'radial-gradient(ellipse, rgba(16,185,129,0.15) 0%, transparent 70%)', filter: 'blur(40px)', pointerEvents: 'none' }} />
        <div style={{ position: 'relative', zIndex: 1, maxWidth: '680px', margin: '0 auto' }}>
          <div style={{ width: '72px', height: '72px', background: 'linear-gradient(135deg, #10b981, #059669)', borderRadius: '20px', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 28px', boxShadow: '0 0 40px rgba(16,185,129,0.5)', fontSize: '2rem', fontWeight: 900, color: '#fff' }}>U</div>
          <h2 style={{ fontSize: 'clamp(2rem, 4vw, 3rem)', fontWeight: 900, letterSpacing: '-1.5px', marginBottom: '20px' }}>
            Ready to digitize your<br /><span style={{ color: '#10b981' }}>community management?</span>
          </h2>
          <p style={{ color: '#64748b', fontSize: '1rem', marginBottom: '40px', lineHeight: 1.7 }}>
            Join hundreds of residential societies already running smarter with UrbanLife Community OS.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', flexWrap: 'wrap' }}>
            <Link to="/register" style={{
              display: 'inline-flex', alignItems: 'center', gap: '8px',
              padding: '18px 42px', borderRadius: '14px', fontWeight: 800, fontSize: '1.05rem',
              color: '#fff', textDecoration: 'none',
              background: 'linear-gradient(135deg, #10b981, #059669)',
              boxShadow: '0 8px 30px rgba(16,185,129,0.5)',
              transition: 'all 0.2s',
            }}>
              Start for Free <ArrowRight size={18} />
            </Link>
            <Link to="/login" style={{
              display: 'inline-flex', alignItems: 'center', gap: '8px',
              padding: '18px 42px', borderRadius: '14px', fontWeight: 700, fontSize: '1.05rem',
              color: '#f1f5f9', textDecoration: 'none',
              border: '1px solid rgba(255,255,255,0.2)', background: 'rgba(255,255,255,0.05)',
              transition: 'all 0.2s',
            }}>
              Sign In
            </Link>
          </div>
        </div>
      </section>

      {/* ── Footer ── */}
      <footer style={{ borderTop: '1px solid rgba(30,41,59,0.8)', padding: '32px 48px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ width: '32px', height: '32px', background: 'linear-gradient(135deg, #10b981, #059669)', borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800, color: '#fff', fontSize: '1rem' }}>U</div>
          <span style={{ fontWeight: 700, fontSize: '1rem' }}>UrbanLife</span>
          <span style={{ color: '#334155', fontSize: '0.85rem' }}>Community Operating System</span>
        </div>
        <p style={{ color: '#334155', fontSize: '0.82rem' }}>
          © 2026 UrbanLife. Built with ❤️ using Spring Boot + React. All rights reserved.
        </p>
      </footer>

      <style>{`
        @keyframes bounce {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(8px); }
        }
      `}</style>
    </div>
  );
}
